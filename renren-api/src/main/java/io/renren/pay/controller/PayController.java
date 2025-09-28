package io.renren.pay.controller;


import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import io.renren.annotation.Login;

import io.renren.annotation.LoginUser;
import io.renren.async.ShareAsync;
import io.renren.common.utils.Result;
import io.renren.dto.PayDTO;
import io.renren.entity.*;
import io.renren.pay.service.PayService;
import io.renren.pay.utils.CallMethod;
import io.renren.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * 统一下单支付接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api")
@Api(tags="统一下单支付接口")
public class PayController {
	
    @Autowired
    private PayConfigService payConfigService;
    @Autowired
    private PayService payService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ShareAsync shareAsync;
	
    @Login
    @PostMapping("pay")
    @ApiOperation("充值")
    @Transactional
    public Result<Map<String, Object>> pay(@LoginUser UserEntity user,@RequestBody PayDTO payDTO) throws ParseException, WxPayException{
    	//拿到通道ID  当在微信公众号或者小程序时  通道ID就会变成通道类型   2:微信公众号  4:微信小程序
    	Long payId = payDTO.getPayId();
    	//拿到用户ID
    	Long userId = user.getId();
    	//拿到用户OPENID
    	String wxOpenid = user.getWxOpenid();
    	payDTO.setUserId(userId);
    	payDTO.setOpenId(wxOpenid);
    	PayConfigEntity payConfig = null;
    	//封装查询条件
    	Map<String, Object> map = new HashMap<>();
    	if(payId == 2 || payId == 4) {
    		
    		//根据通道类型查询支付配置
    		map.put("type", payId);
    		payConfig = payConfigService.getPayConfigByType(map);
    		
    	}else {
    		
        	//根据通道ID查询充值通道数据
    		map.put("id", payId);
    		payConfig = payConfigService.getPayConfigById(map);
        	
		}
    	
    	if(payConfig == null) {
    		
    		return new Result<Map<String, Object>>().error("支付配置错误,请联系官方人员!");
    		
    	}

    	//拿到该调用的方法
    	String callName = payConfig.getCallName();
//    	if(callName.equals("WX_XCX")) {
//    		Map<String, Object> WX_XCX = payService2.WX_XCX(payDTO);
//    		return new Result<Map<String, Object>>().ok(WX_XCX);
//    	}
    	
    	//封装支付数据
    	payDTO.setPayId(payConfig.getId());
    	
        Class[] param={PayDTO.class};
        Object[] params={payDTO};
        //通过反射根据方法名找到对应的支付方法
        @SuppressWarnings("unchecked")
		Map<String, Object> call = (Map<String, Object>) CallMethod.call(payService,callName,param,params);
		return new Result<Map<String, Object>>().ok(call);
        
    }
    
    
    /**
     * 
     * 微信小程序支付回调
     * @throws WxPayException 
     * 
     * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码 type传(6)  支付宝PC扫码 type传(7)
     * 
     */
    @PostMapping("wx_xcx_notify")
    @ApiOperation("微信充值回调接口-微信小程序")
    public String wx_xcx_notify(@RequestBody String xmlData) throws WxPayException, ParseException {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 4);
        PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
    	//拿到APPID   小程序/公众号
    	String appId = payConfigByType.getAppId();
    	//拿到商户号
    	String merchantId = payConfigByType.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigByType.getAppSecret();
        
        
        WxPayService wxPayService = new WxPayServiceImpl();
        /******封装解密数据**************************************************/
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
	    payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
			
        WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
        String outtradeno = notifyResult.getOutTradeNo();//商户订单号
        String tradeType = notifyResult.getResultCode();
        if(tradeType.equals("SUCCESS")){
            //拿到三方订单号
            String trilateralCode = notifyResult.getTransactionId();
            //根据订单号查询订单数据
            OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
            if(orderByCode == null) {
            	return WxPayNotifyResponse.success("处理成功!");
            }
            orderByCode.setOrderType(2);
            orderByCode.setTrilateralCode(trilateralCode);
            //修改订单状态
            orderService.updateById(orderByCode);

            //为用户加上购买的产品
            shareAsync.updateUserVIPorNumberOne(orderByCode);

            //如果充值的是次数    异步为用户添加问答次数流水
            if(orderByCode.getCommodityType() == 1) {
                shareAsync.questionsFlowingWater(orderByCode);
            }

            //异步为上级返佣(两级)
            shareAsync.InviteRebaterOne(orderByCode);
            //异步修改支付通道已成功金额
            shareAsync.amountSuccess(orderByCode);
            
            return WxPayNotifyResponse.success("支付成功!");
        }
        return null;

    }
    
    
    
    /**
     * 
     * 微信公众号支付回调
     * @throws WxPayException 
     * 
     * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码 type传(6)  支付宝PC扫码 type传(7)
     * 
     */
    @PostMapping("wx_gzh_notify")
    @ApiOperation("微信充值回调接口-微信公众号")
    public String wx_gzh_notify(@RequestBody String xmlData) throws WxPayException, ParseException {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 2);
        PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
    	//拿到APPID   小程序/公众号
    	String appId = payConfigByType.getAppId();
    	//拿到商户号
    	String merchantId = payConfigByType.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigByType.getAppSecret();
        
        
        WxPayService wxPayService = new WxPayServiceImpl();
        /******封装解密数据**************************************************/
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
	    payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
			
        WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
        String outtradeno = notifyResult.getOutTradeNo();//商户订单号
        String tradeType = notifyResult.getResultCode();
        if(tradeType.equals("SUCCESS")){
            //拿到三方订单号
            String trilateralCode = notifyResult.getTransactionId();
            //根据订单号查询订单数据
            OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
            if(orderByCode == null) {
            	return WxPayNotifyResponse.success("支付成功!");
            }
            orderByCode.setOrderType(2);
            orderByCode.setTrilateralCode(trilateralCode);
            //修改订单状态
            orderService.updateById(orderByCode);

            //为用户加上购买的产品
            shareAsync.updateUserVIPorNumberOne(orderByCode);

            //如果充值的是次数    异步为用户添加问答次数流水
            if(orderByCode.getCommodityType() == 1) {
                shareAsync.questionsFlowingWater(orderByCode);
            }

            //异步为上级返佣(两级)
            shareAsync.InviteRebaterOne(orderByCode);
            //异步修改支付通道已成功金额
            shareAsync.amountSuccess(orderByCode);
            
            return WxPayNotifyResponse.success("支付成功!");
        }
        return null;

    }
    
    /**
     * 
     * 微信H5支付回调
     * @throws WxPayException 
     * 
     * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码 type传(6)  支付宝PC扫码 type传(7)
     * 
     */
    @PostMapping("wx_h5_notify")
    @ApiOperation("微信充值回调接口-微信H5")
    public String wx_h5_notify(@RequestBody String xmlData) throws WxPayException, ParseException {
        Map<String, Object> map = new HashMap<>();
        map.put("type", 3);
        PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
    	//拿到APPID   小程序/公众号
    	String appId = payConfigByType.getAppId();
    	//拿到商户号
    	String merchantId = payConfigByType.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigByType.getAppSecret();
        
        
        WxPayService wxPayService = new WxPayServiceImpl();
        /******封装解密数据**************************************************/
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(appId);
	    payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
			
        WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
        String outtradeno = notifyResult.getOutTradeNo();//商户订单号
        String tradeType = notifyResult.getResultCode();
        if(tradeType.equals("SUCCESS")){
            //拿到三方订单号
            String trilateralCode = notifyResult.getTransactionId();
            //根据订单号查询订单数据
            OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
            if(orderByCode == null) {
            	return WxPayNotifyResponse.success("支付成功!");
            }
            orderByCode.setOrderType(2);
            orderByCode.setTrilateralCode(trilateralCode);
            //修改订单状态
            orderService.updateById(orderByCode);

            //为用户加上购买的产品
            shareAsync.updateUserVIPorNumberOne(orderByCode);

            //如果充值的是次数    异步为用户添加问答次数流水
            if(orderByCode.getCommodityType() == 1) {
                shareAsync.questionsFlowingWater(orderByCode);
            }

            //异步为上级返佣(两级)
            shareAsync.InviteRebaterOne(orderByCode);
            //异步修改支付通道已成功金额
            shareAsync.amountSuccess(orderByCode);
            
            return WxPayNotifyResponse.success("支付成功!");
        }
		return null;
        

    }
    



	/**
	 * 
	 * 微信PC扫码支付回调
	 * @throws WxPayException 
	 * 
	 * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码 type传(6)  支付宝PC扫码 type传(7)
	 * 
	 */
	@PostMapping("wx_native_notify")
	@ApiOperation("微信PC扫码支付回调接口-微信NATIVE")
	public String wx_native_notify(@RequestBody String xmlData) throws WxPayException, ParseException {
	    Map<String, Object> map = new HashMap<>();
	    map.put("type", 6);
	    PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
		//拿到APPID   小程序/公众号
		String appId = payConfigByType.getAppId();
		//拿到商户号
		String merchantId = payConfigByType.getMerchantId();
		//拿到商户密钥
		String appSecret = payConfigByType.getAppSecret();
	    
	    
	    WxPayService wxPayService = new WxPayServiceImpl();
	    /******封装解密数据**************************************************/
	    WxPayConfig payConfig = new WxPayConfig();
	    payConfig.setAppId(appId);
	    payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
			
	    WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlData);
	    String outtradeno = notifyResult.getOutTradeNo();//商户订单号
	    String tradeType = notifyResult.getResultCode();
	    if(tradeType.equals("SUCCESS")){
	        //拿到三方订单号
	        String trilateralCode = notifyResult.getTransactionId();
	        //根据订单号查询订单数据
	        OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
	        if(orderByCode == null) {
	        	return WxPayNotifyResponse.success("支付成功!");
	        }
	        orderByCode.setOrderType(2);
	        orderByCode.setTrilateralCode(trilateralCode);
	        //修改订单状态
	        orderService.updateById(orderByCode);
	
	        //为用户加上购买的产品
	        shareAsync.updateUserVIPorNumberOne(orderByCode);
	
	        //如果充值的是次数    异步为用户添加问答次数流水
	        if(orderByCode.getCommodityType() == 1) {
	            shareAsync.questionsFlowingWater(orderByCode);
	        }
	
	        //异步为上级返佣(两级)
	        shareAsync.InviteRebaterOne(orderByCode);
	        //异步修改支付通道已成功金额
	        shareAsync.amountSuccess(orderByCode);
	        
	        return WxPayNotifyResponse.success("支付成功!");
	    }
		return null;
	    
	
	}
    
    
    
    /**
     * 
     * 支付宝H5支付回调
     * @throws ParseException 
     * @throws NumberFormatException 
     * @throws WxPayException 
     * 
     * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码 type传(6)  支付宝PC扫码 type传(7)
     * 
     */
    @PostMapping("ali_h5_notify")
    @ApiOperation("支付宝H5支付回调-支付宝H5")
    public String ali_h5_notify(HttpServletRequest request) throws NumberFormatException, ParseException{
        Map<String, Object> map = new HashMap<>();
        map.put("type", 1);
        PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
    	//拿到商户公钥
    	String publicKey = payConfigByType.getPublicKey();
        Map requestParams = request.getParameterMap();
        String q = "";
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            for (int i = 0; i < values.length; i++) {
                q+=name+"="+values[i]+"&";
            }
        }

        System.out.println("支付宝支付结果通知"+requestParams.toString());
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();

        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        try {
            //验证签名
            boolean flag = AlipaySignature.rsaCheckV1(params, publicKey, "utf-8","RSA2");
            if(flag){
            	//判断订单状态 
                if("TRADE_SUCCESS".equals(params.get("trade_status"))){//订单支付完成
                	//拿到三方订单号
                	String trilateralCode = params.get("trade_no");
                    //商户订单号
                    String outtradeno = params.get("out_trade_no");
                    //根据订单号查询订单数据
                    OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
                    if(orderByCode == null) {
                    	return "success";
                    }
                    orderByCode.setOrderType(2);
                    orderByCode.setTrilateralCode(trilateralCode);
                    //修改订单状态
                    orderService.updateById(orderByCode);
                    
                    //为用户加上购买的产品
                    shareAsync.updateUserVIPorNumberOne(orderByCode);
                    
                    //如果充值的是次数    异步为用户添加问答次数流水
                    if(orderByCode.getCommodityType() == 1) {
                    	shareAsync.questionsFlowingWater(orderByCode);
                    }
                    
                    //异步为上级返佣(两级)
                    shareAsync.InviteRebaterOne(orderByCode);
                    //异步修改支付通道已成功金额
                    shareAsync.amountSuccess(orderByCode);
                    
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "success";
    }
    
    
    /**
     * 
     * 支付宝PC扫码支付回调
     * @throws ParseException 
     * @throws NumberFormatException 
     * @throws WxPayException 
     * 
     * type:公众号type传(2)  小程序type传(4) 微信H5type传(3)  微信PC扫码type传(6)  支付宝PC扫码type传(7)
     * 
     */
    @PostMapping("ali_instant_notify")
    @ApiOperation("支付宝PC扫码支付回调-支付宝FAST_INSTANT_TRADE_PAY")
    public String ali_instant_notify(HttpServletRequest request) throws NumberFormatException, ParseException{
        Map<String, Object> map = new HashMap<>();
        map.put("type", 7);
        PayConfigEntity payConfigByType = payConfigService.getPayConfigByType(map);
    	//拿到商户公钥
    	String publicKey = payConfigByType.getPublicKey();
        Map requestParams = request.getParameterMap();
        String q = "";
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            for (int i = 0; i < values.length; i++) {
                q+=name+"="+values[i]+"&";
            }
        }

        System.out.println("支付宝支付结果通知"+requestParams.toString());
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();

        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        try {
            //验证签名
            boolean flag = AlipaySignature.rsaCheckV1(params, publicKey, "utf-8","RSA2");
            if(flag){
            	//判断订单状态 
                if("TRADE_SUCCESS".equals(params.get("trade_status"))){//订单支付完成
                	//拿到三方订单号
                	String trilateralCode = params.get("trade_no");
                    //商户订单号
                    String outtradeno = params.get("out_trade_no");
                    //根据订单号查询订单数据
                    OrderEntity orderByCode = orderService.getOrderByCode(outtradeno);
                    if(orderByCode == null) {
                    	return "success";
                    }
                    orderByCode.setOrderType(2);
                    orderByCode.setTrilateralCode(trilateralCode);
                    //修改订单状态
                    orderService.updateById(orderByCode);
                    
                    //为用户加上购买的产品
                    shareAsync.updateUserVIPorNumberOne(orderByCode);
                    
                    //如果充值的是次数    异步为用户添加问答次数流水
                    if(orderByCode.getCommodityType() == 1) {
                    	shareAsync.questionsFlowingWater(orderByCode);
                    }
                    
                    //异步为上级返佣(两级)
                    shareAsync.InviteRebaterOne(orderByCode);
                    //异步修改支付通道已成功金额
                    shareAsync.amountSuccess(orderByCode);
                    
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "success";
    }
    
    
    
    public static Map<String, String> paraFilter(Map<String, String> sArray)
    {
        Map<String, String> result = new HashMap<String, String>(sArray.size());
        if (sArray == null || sArray.size() <= 0)
        {
            return result;
        }
        for (String key : sArray.keySet())
        {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign"))
            {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }
	public static void buildPayParams(StringBuilder sb, Map<String, String> payParams, boolean encoding)
    {
        List<String> keys = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys);
        for(String key : keys)
        {
            sb.append(key).append("=");
            if(encoding)
            {
                sb.append(urlEncode(payParams.get(key)));
            }
            else
            {
                sb.append(payParams.get(key));
            }
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
    }
	public static String urlEncode(String str)
    {
        try
        {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (Throwable e)
        {
            return str;
        }
    }
	
	
}