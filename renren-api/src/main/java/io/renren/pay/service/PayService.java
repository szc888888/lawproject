package io.renren.pay.service;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
//import com.github.wxpay.sdk.WXPayUtil;

import io.renren.common.utils.Result;
import io.renren.dto.CommodityDTO;
import io.renren.dto.OrderDTO;
import io.renren.dto.PayConfigDTO;
import io.renren.dto.PayDTO;
import io.renren.entity.UserEntity;
import io.renren.service.CommodityService;
import io.renren.service.OrderService;
import io.renren.service.PayConfigService;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PayService {
	
	@Resource
    private CommodityService commodityService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PayConfigService payConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysParamsService paramsService;
    
    private static PayService payService;
    
    @PostConstruct
    public void init() {
    	payService = this;
    	payService.commodityService = this.commodityService;
    	payService.orderService = this.orderService;
    	payService.payConfigService = this.payConfigService;
    	payService.userService = this.userService;
    	payService.paramsService = this.paramsService;
    }
	
	
    /**
     * 
     * 微信小程序支付(JSAPI)
     * 
     * */
    public Map<String, Object> WX_XCX_PAY(PayDTO payDTO) throws ParseException, WxPayException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice().setScale(2,BigDecimal.ROUND_DOWN);
		           commodityPrice = commodityPrice.multiply(new BigDecimal(100));
    	//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
    	//下单用户openId
    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID   小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户号
    	String merchantId = payConfigDTO.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigDTO.getAppSecret();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//生成订单号
    	String orderCode = "WX_XCX_PAY_"+code();
    	//购买商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
    	
    	//查询小程序appid
    	String appAppidCode = payService.paramsService.getValue(payConfigDTO.getAppAppidCode());
    	//查询小程序密钥
    	String appSecretCode = payService.paramsService.getValue(payConfigDTO.getAppSecretCode());
    	
    	
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(appId);
		payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
//		payConfig.setKeyPath("");
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
//        wxPayService = new WxPayServiceImpl();
        //添加微信支付请求数据
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderCode);
        orderRequest.setTotalFee(commodityPrice.intValue());//金额
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setBody("智能问答:"+commodityName+"-充值");


        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);	
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(orderRequest);
        Map<String,Object> jsConfig = new HashMap<>();
        jsConfig.put("appId",wxPayMpOrderResult.getAppId());
        jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
        jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
        jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
        jsConfig.put("signType",wxPayMpOrderResult.getSignType());
        jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
        

    	
        /****************************支付请求成功后订单添加记录*******************************************************/
    	//查询用户数据
    	UserEntity userEntity = payService.userService.getUserByUserId(userId);
        OrderDTO orderDTO = new OrderDTO();
    	orderDTO.setUserId(userId);//充值用户ID
    	orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	orderDTO.setCommodityId(commodityId);//充值商品ID
    	orderDTO.setCommodityName(commodityName);//充值商品名称
    	orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	orderDTO.setPayId(payId);//充值通道ID
    	orderDTO.setPayType(type);
    	orderDTO.setOrderCode(orderCode);//平台订单号
    	orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	orderDTO.setRechargeAmount(commodityDTO.getCommodityPrice());//充值金额(元)
    	orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	orderDTO.setFoundTime(new Date());//订单创建时间
    	orderDTO.setOperateTime(new Date());//订单操作时间
    	orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	payService.orderService.save(orderDTO);
    	//下一步返回支付链接
		return jsConfig;
    	
	}
    
    /**
     * 
     * 微信公众号支付(JSAPI)
     * 
     * */
    public Map<String, Object> WX_GZH_PAY(PayDTO payDTO) throws ParseException, WxPayException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice().setScale(2,BigDecimal.ROUND_DOWN);
    			   commodityPrice = commodityPrice.multiply(new BigDecimal(100));
    	//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
    	//下单用户openId
    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID  小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户号
    	String merchantId = payConfigDTO.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigDTO.getAppSecret();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//生成订单号
    	String orderCode = "WX_GZH_PAY_"+code();
    	//充值商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
    	
    	//查询小程序appid
    	String appAppidCode = payService.paramsService.getValue(payConfigDTO.getAppAppidCode());
    	//查询小程序密钥
    	String appSecretCode = payService.paramsService.getValue(payConfigDTO.getAppSecretCode());
    	
    	
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(appId);
		payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
//		payConfig.setKeyPath("");
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
//        wxPayService = new WxPayServiceImpl();
        //添加微信支付请求数据
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderCode);
        orderRequest.setTotalFee(commodityPrice.intValue());//金额
        orderRequest.setSpbillCreateIp("127.0.0.1");
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setBody("智能问答:"+commodityName+"-充值");


        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(openId);	
        WxPayMpOrderResult wxPayMpOrderResult = wxPayService.createOrder(orderRequest);
        Map<String,Object> jsConfig = new HashMap<>();
        jsConfig.put("appId",wxPayMpOrderResult.getAppId());
        jsConfig.put("timestamp",wxPayMpOrderResult.getTimeStamp());
        jsConfig.put("nonceStr",wxPayMpOrderResult.getNonceStr());
        jsConfig.put("package",wxPayMpOrderResult.getPackageValue());
        jsConfig.put("signType",wxPayMpOrderResult.getSignType());
        jsConfig.put("paySign",wxPayMpOrderResult.getPaySign());
        

    	
        /****************************支付请求成功后订单添加记录*******************************************************/
    	//查询用户数据
    	UserEntity userEntity = payService.userService.getUserByUserId(userId);
        OrderDTO orderDTO = new OrderDTO();
    	orderDTO.setUserId(userId);//充值用户ID
    	orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	orderDTO.setCommodityId(commodityId);//充值商品ID
    	orderDTO.setCommodityName(commodityName);//充值商品名称
    	orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	orderDTO.setPayId(payId);//充值通道ID
    	orderDTO.setPayType(type);
    	orderDTO.setOrderCode(orderCode);//平台订单号
    	orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	orderDTO.setRechargeAmount(commodityDTO.getCommodityPrice());//充值金额(元)
    	orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	orderDTO.setFoundTime(new Date());//订单创建时间
    	orderDTO.setOperateTime(new Date());//订单操作时间
    	orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	payService.orderService.save(orderDTO);
    	//下一步返回支付链接
		return jsConfig;
    	
	}
    
    
    /**
     * 
     * 微信H5支付(MWEB)
     * 
     * */
    public Map<String, Object> WX_H5_PAY(PayDTO payDTO) throws ParseException, WxPayException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice().setScale(2,BigDecimal.ROUND_DOWN);
		           commodityPrice = commodityPrice.multiply(new BigDecimal(100));//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
//    	下单用户openId
//    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID   小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户号
    	String merchantId = payConfigDTO.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigDTO.getAppSecret();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//生成订单号
    	String orderCode = "WX_H5_PAY_"+code();
    	//充值商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
    	//查询用户数据
    	UserEntity userEntity = payService.userService.getUserByUserId(userId);
    	
    	//查询小程序appid
    	String appAppidCode = payService.paramsService.getValue(payConfigDTO.getAppAppidCode());
    	//查询小程序密钥
    	String appSecretCode = payService.paramsService.getValue(payConfigDTO.getAppSecretCode());
    	
    	try {
    		
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(appId);
		payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
//		payConfig.setKeyPath("");
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
//        wxPayService = new WxPayServiceImpl();
        //添加微信支付请求数据
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderCode);
        orderRequest.setTotalFee(commodityPrice.intValue());//金额
        orderRequest.setSpbillCreateIp(userEntity.getIp());
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setBody("智能问答:"+commodityName+"-充值");
        orderRequest.setTradeType("MWEB");
        WxPayMwebOrderResult wxPayMwebOrderResult =  wxPayService.createOrder(orderRequest);
        Map<String,Object> jsConfig = new HashMap<>();
        String mwebUrl = wxPayMwebOrderResult.getMwebUrl();
//        jsConfig.put("mweb_url",mwebUrl);
        jsConfig.put("payLink", mwebUrl);
        
    	
        /****************************支付请求成功后订单添加记录*******************************************************/
        OrderDTO orderDTO = new OrderDTO();
    	orderDTO.setUserId(userId);//充值用户ID
    	orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	orderDTO.setCommodityId(commodityId);//充值商品ID
    	orderDTO.setCommodityName(commodityName);//充值商品名称
    	orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	orderDTO.setPayId(payId);//充值通道ID
    	orderDTO.setPayType(type);
    	orderDTO.setOrderCode(orderCode);//平台订单号
    	orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	orderDTO.setRechargeAmount(commodityDTO.getCommodityPrice());//充值金额(元)
    	orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	orderDTO.setFoundTime(new Date());//订单创建时间
    	orderDTO.setOperateTime(new Date());//订单操作时间
    	orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	payService.orderService.save(orderDTO);
    	
    	//下一步返回支付链接
		return jsConfig;
		
		} catch (Exception e) {
			log.info("调用微信H5支付错误:",e.getMessage());
		}
    	
		return null;
	}
    
    
    
    /**
     * 
     * 微信NATIVE支付(NATIVE PC扫码)
     * 
     * */
    public Map<String, Object> WX_NATIVE_PAY(PayDTO payDTO) throws ParseException, WxPayException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice().setScale(2,BigDecimal.ROUND_DOWN);
		           commodityPrice = commodityPrice.multiply(new BigDecimal(100));//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
//    	下单用户openId
//    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID   小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户号
    	String merchantId = payConfigDTO.getMerchantId();
    	//拿到商户密钥
    	String appSecret = payConfigDTO.getAppSecret();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//生成订单号
    	String orderCode = "WX_SM_PAY_"+code();
    	//充值商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
    	//查询用户数据
    	UserEntity userEntity = payService.userService.getUserByUserId(userId);
    	
    	//查询小程序appid
    	String appAppidCode = payService.paramsService.getValue(payConfigDTO.getAppAppidCode());
    	//查询小程序密钥
    	String appSecretCode = payService.paramsService.getValue(payConfigDTO.getAppSecretCode());
    	
    	try {
    		
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
		payConfig.setAppId(appId);
		payConfig.setMchId(merchantId);
		payConfig.setMchKey(appSecret);
//		payConfig.setKeyPath("");
		// 可以指定是否使用沙箱环境
		payConfig.setUseSandboxEnv(false);
		wxPayService.setConfig(payConfig);
//        wxPayService = new WxPayServiceImpl();
        //添加微信支付请求数据
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setOutTradeNo(orderCode);
        orderRequest.setTotalFee(commodityPrice.intValue());//金额
        orderRequest.setSpbillCreateIp(userEntity.getIp());
        orderRequest.setNotifyUrl(notifyUrl);
        orderRequest.setBody("智能问答:"+commodityName+"-充值");
        orderRequest.setTradeType("NATIVE");
        orderRequest.setProductId(code());
        WxPayNativeOrderResult wxPayMwebOrderResult =  wxPayService.createOrder(orderRequest);
        Map<String,Object> jsConfig = new HashMap<>();
        String codeUrl = wxPayMwebOrderResult.getCodeUrl();
//        jsConfig.put("mweb_url",mwebUrl);
        //支付链接
        jsConfig.put("payLink", codeUrl);
        
    	
        /****************************支付请求成功后订单添加记录*******************************************************/
        OrderDTO orderDTO = new OrderDTO();
    	orderDTO.setUserId(userId);//充值用户ID
    	orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	orderDTO.setCommodityId(commodityId);//充值商品ID
    	orderDTO.setCommodityName(commodityName);//充值商品名称
    	orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	orderDTO.setPayId(payId);//充值通道ID
    	orderDTO.setPayType(type);
    	orderDTO.setOrderCode(orderCode);//平台订单号
    	orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	orderDTO.setRechargeAmount(commodityDTO.getCommodityPrice());//充值金额(元)
    	orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	orderDTO.setFoundTime(new Date());//订单创建时间
    	orderDTO.setOperateTime(new Date());//订单操作时间
    	orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	payService.orderService.save(orderDTO);
    	
    	jsConfig.put("OrderId", orderDTO.getId());
    	//向前端返回支付类型
    	jsConfig.put("type", type);
    	
    	//下一步返回支付链接
		return jsConfig;
		
		} catch (Exception e) {
			log.info("调用微信NATIVE扫码支付错误:",e.getMessage());
		}
    	
		return null;
	}
    
    
    
    /**
     * 
     * 支付宝H5支付(QUICK_MSECURITY_PAY)
     * 
     * */
    public Map<String, Object> ALI_H5_PAY(PayDTO payDTO) throws ParseException, WxPayException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice();
    	//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
//    	下单用户openId
//    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID   小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户私钥
    	String privateKey = payConfigDTO.getPrivateKey();
    	//拿到商户公钥
    	String publicKey = payConfigDTO.getPublicKey();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//拿到支付完成后跳转地址
    	String returnUrl = payConfigDTO.getReturnUrl();
    	//生成订单号
    	String orderCode = "ALI_H5_PAY_"+code();
    	//购买商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
    	
    	//查询小程序appid
    	String appAppidCode = payService.paramsService.getValue(payConfigDTO.getAppAppidCode());
    	//查询小程序密钥
    	String appSecretCode = payService.paramsService.getValue(payConfigDTO.getAppSecretCode());
    	
			
    	//网关地址,APPID,商户应用私钥,数据格式,编码格式,支付宝公钥,签名算法类型
    	AlipayClient alipayClient = new DefaultAlipayClient(payUrl,appId,privateKey,"json","utf-8",publicKey,"RSA2");//不同支付类型需使用不同的请求对象
    	AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
    	//请求参数集合对象,除了公共参数之外,所有参数都可通过此对象传递（不同支付类型需使用不同的请求参数对象）
    	AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
    	//订单描述
    	model.setBody("智能问答充值");
    	//订单标题
    	model.setSubject("智能问答:"+commodityName+"-充值");
    	//商户订单号
    	model.setOutTradeNo(orderCode);
    	//订单的过期时长(取值为5m - 15d,即五分钟到十五天)
    	model.setTimeoutExpress("5m");
    	//订单总金额
    	model.setTotalAmount(String.valueOf(commodityPrice));
    	//产品码  QUICK_WAP_WAY:无线快捷支付产品
    	model.setProductCode("QUICK_MSECURITY_PAY");
    	//用户付款中途退出返回商户网站的地址
    	model.setQuitUrl(returnUrl);
    	request.setBizModel(model);
    	//支付成功后的跳转地址
    	request.setReturnUrl(returnUrl);
    	//支付成功后的回调地址（此地址必须为公网地址，用于支付宝收到用户付款之后,通知我们的服务端,我们可以在此接口中更改订单状态为已付款或后续操作）
    	request.setNotifyUrl(notifyUrl);
//    	String orderStr = "";
//    	AlipayTradeWapPayResponse responseH5 = null;
    	try {
    		
    		String payLink = alipayClient.pageExecute(request,"GET").getBody();
//    		responseH5 = alipayClient.pageExecute(request);
    	    /****************************支付请求成功后订单添加记录*******************************************************/
    	   //查询用户数据
    	    UserEntity userEntity = payService.userService.getUserByUserId(userId);
    	    OrderDTO orderDTO = new OrderDTO();
    	    orderDTO.setUserId(userId);//充值用户ID
    	    orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	    orderDTO.setCommodityId(commodityId);//充值商品ID
    	    orderDTO.setCommodityName(commodityName);//充值商品名称
    	    orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	    orderDTO.setPayId(payId);//充值通道ID
    	    orderDTO.setPayType(type);
    	    orderDTO.setOrderCode(orderCode);//平台订单号
    	    orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	    orderDTO.setRechargeAmount(commodityPrice);//充值金额(元)
    	    orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	    orderDTO.setFoundTime(new Date());//订单创建时间
    	    orderDTO.setOperateTime(new Date());//订单操作时间
    	    orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	    payService.orderService.save(orderDTO);
    	    	
    	    //封装返回数据
    	    Map<String,Object> jsConfig = new HashMap<>();
    	    jsConfig.put("payLink", payLink);
    	    
    	    //下一步返回支付链接
    		return jsConfig;
    		   
    	} catch (AlipayApiException e) {
    		
    		log.info("调用支付宝H5支付错误:",e.getMessage());
    		
    	}
		return null;
	}
    
    
    /**
     * 
     * 支付宝PC扫码支付(FAST_INSTANT_TRADE_PAY)
     * @throws AlipayApiException 
     * 
     * */
    public Map<String, Object> ALI_INSTANT_TRADE_PAY(PayDTO payDTO) throws ParseException, WxPayException, AlipayApiException{
		//拿到用户ID
		Long userId = payDTO.getUserId();
		//拿到充值商品ID
    	Long commodityId = payDTO.getCommodityId();
    	//拿到通道ID
    	Long payId = payDTO.getPayId();
		//根据商品ID查询商品详细信息
    	CommodityDTO commodityDTO = payService.commodityService.get(commodityId);
    	if(commodityDTO == null) {
    		new Result<Map<String, Object>>().error("商品已下架,请重新选择!");
    	}
    	//拿到商品的价值(人民币)
    	BigDecimal commodityPrice = commodityDTO.getCommodityPrice();
    	//根据通道ID查询充值通道数据
    	PayConfigDTO payConfigDTO = payService.payConfigService.get(payId);
    	if(payConfigDTO == null) {
    		new Result<Map<String, Object>>().error("请选择一个充值通道!");
    	}
    	/****************************这里是请求支付流程*******************************************************/
//    	下单用户openId
//    	String openId = payDTO.getOpenId();
    	//通道类型   1:支付宝支付    2:微信支付
    	Integer type = payConfigDTO.getType();
    	//拿到APPID   小程序/公众号
    	String appId = payConfigDTO.getAppId();
    	//拿到商户私钥
    	String privateKey = payConfigDTO.getPrivateKey();
    	//拿到商户公钥
    	String publicKey = payConfigDTO.getPublicKey();
    	//回调地址
    	String notifyUrl = payConfigDTO.getNotifyUrl();
    	//支付网关
    	String payUrl = payConfigDTO.getPayUrl();
    	//拿到支付完成后跳转地址
    	String returnUrl = payConfigDTO.getReturnUrl();
    	//生成订单号
    	String orderCode = "ALI_SM_PAY_"+code();
    	//购买商品名称
    	String commodityName = commodityDTO.getCommodityName();
    	
//    	//网关地址,APPID,商户应用私钥,数据格式,编码格式,支付宝公钥,签名算法类型
//    	AlipayClient alipayClient = new DefaultAlipayClient(payUrl,appId,privateKey,"json","utf-8",publicKey,"RSA2");//不同支付类型需使用不同的请求对象
//    	AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
//    	//请求参数集合对象,除了公共参数之外,所有参数都可通过此对象传递（不同支付类型需使用不同的请求参数对象）
//    	AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
//    	//订单描述
//    	model.setBody("智能问答充值");
//    	//订单标题
//    	model.setSubject("智能问答:"+commodityName+"-充值");
//    	//商户订单号
//    	model.setOutTradeNo(orderCode);
//    	//订单的过期时长(取值为5m - 15d,即五分钟到十五天)
//    	model.setTimeoutExpress("5m");
//    	//订单总金额
//    	model.setTotalAmount(String.valueOf(commodityPrice));
//    	//产品码  FAST_INSTANT_TRADE_PAY:支付宝里PC扫码支付
//    	model.setProductCode("FAST_INSTANT_TRADE_PAY");
//    	//用户付款中途退出返回商户网站的地址
//    	model.setQuitUrl(returnUrl);
//    	request.setBizModel(model);
//    	//支付成功后的跳转地址
//    	request.setReturnUrl(returnUrl);
//    	//支付成功后的回调地址（此地址必须为公网地址，用于支付宝收到用户付款之后,通知我们的服务端,我们可以在此接口中更改订单状态为已付款或后续操作）
//    	request.setNotifyUrl(notifyUrl);
////    	String orderStr = "";
////    	AlipayTradeWapPayResponse responseH5 = null;
    	
    	
    	AlipayClient alipayClient = new DefaultAlipayClient(payUrl,appId,privateKey,"json","utf-8",publicKey,"RSA2");
    	AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
    	//异步接收地址，仅支持http/https，公网可访问
    	request.setNotifyUrl(notifyUrl);
    	//同步跳转地址，仅支持http/https
    	request.setReturnUrl(returnUrl);
    	/******必传参数******/
    	JSONObject bizContent = new JSONObject();
    	//商户订单号，商家自定义，保持唯一性
    	bizContent.put("out_trade_no", orderCode);
    	//支付金额，最小值0.01元
    	bizContent.put("total_amount", String.valueOf(commodityPrice));
    	//订单标题，不可使用特殊符号
    	bizContent.put("subject", "智能问答:"+commodityName+"-充值");
//    	bizContent.put("subject", "goumai");
    	//电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
    	bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

    	/******可选参数******/
    	//bizContent.put("time_expire", "2022-08-01 22:00:00");

    	//// 商品明细信息，按需传入
    	//JSONArray goodsDetail = new JSONArray();
    	//JSONObject goods1 = new JSONObject();
    	//goods1.put("goods_id", "goodsNo1");
    	//goods1.put("goods_name", "子商品1");
    	//goods1.put("quantity", 1);
    	//goods1.put("price", 0.01);
    	//goodsDetail.add(goods1);
    	//bizContent.put("goods_detail", goodsDetail);

    	//// 扩展信息，按需传入
    	//JSONObject extendParams = new JSONObject();
    	//extendParams.put("sys_service_provider_id", "2088511833207846");
    	//bizContent.put("extend_params", extendParams);

    	request.setBizContent(bizContent.toString());
    	AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
    	if(response.isSuccess()){
    	System.out.println("调用成功");
    	} else {
    	System.out.println("调用失败");
    	}
    	
    	try {
    		
    		String payLink = alipayClient.pageExecute(request,"GET").getBody();
//    		responseH5 = alipayClient.pageExecute(request);
    	    /****************************支付请求成功后订单添加记录*******************************************************/
    	   //查询用户数据
    	    UserEntity userEntity = payService.userService.getUserByUserId(userId);
    	    OrderDTO orderDTO = new OrderDTO();
    	    orderDTO.setUserId(userId);//充值用户ID
    	    orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	    orderDTO.setCommodityId(commodityId);//充值商品ID
    	    orderDTO.setCommodityName(commodityName);//充值商品名称
    	    orderDTO.setCommodityType(commodityDTO.getType());//充值商品类型(1:购买条数  2:购买VIP)
    	    orderDTO.setPayId(payId);//充值通道ID
    	    orderDTO.setPayType(type);
    	    orderDTO.setOrderCode(orderCode);//平台订单号
    	    orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	    orderDTO.setRechargeAmount(commodityPrice);//充值金额(元)
    	    orderDTO.setOrderType(1);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	    orderDTO.setFoundTime(new Date());//订单创建时间
    	    orderDTO.setOperateTime(new Date());//订单操作时间
    	    orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	    payService.orderService.save(orderDTO);
    	    	
    	    //封装返回数据
    	    Map<String,Object> jsConfig = new HashMap<>();
    	    jsConfig.put("payLink", payLink);
        	//向前端返回支付类型
        	jsConfig.put("type", type);
    	    
    	    //下一步返回支付链接
    		return jsConfig;
    		   
    	} catch (AlipayApiException e) {
    		
    		log.info("调用支付宝PC扫码支付错误:",e.getMessage());
    		
    	}
		return null;
	}
	

  /**
   * 
   * 根据当前时间拼接订单号
   * 
   * */
  public static String code() {
        SimpleDateFormat dmDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String randata = getRandom(6);
        Date date = new Date();
        String dateran = dmDate.format(date);
        String Xsode = "" + dateran + randata;
        if (Xsode.length() < 24) {
              Xsode = Xsode + 0;
        }
        return Xsode;
  }

  public static String getRandom(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
              rs.append(r.nextInt(10));
        }
        return rs.toString();
  }
  
  /**
   * 
   * 获取30分钟以后的时间
   * @throws ParseException 
   * 
   * */
  public static Date obtaindate() throws ParseException {
	  long curren = System.currentTimeMillis();
	  curren += 30 * 60 * 1000;
	  Date date = new Date(curren);
	  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String format = dateFormat.format(date);
	  date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(format);
      return date;
}
  
	  /**
	   * 
	   * URL编码
	   * @throws ParseException 
	   * @throws UnsupportedEncodingException 
	   * 
	   * */
	  public static String URLEncoder(String URL) throws ParseException, UnsupportedEncodingException {
		  
		// 将普通字符创转换成application/x-www-from-urlencoded字符串   
		String urlString = URLEncoder.encode(URL, "utf-8");
		
		return urlString;
		
	}
  
  public static void main(String[] args) {

  }
  

  
}