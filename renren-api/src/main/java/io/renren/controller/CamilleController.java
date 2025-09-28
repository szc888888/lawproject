package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.async.ShareAsync;
import io.renren.common.utils.Result;
import io.renren.dto.OrderDTO;
import io.renren.entity.CamilleEntity;
import io.renren.entity.CommodityEntity;
import io.renren.entity.OrderEntity;
import io.renren.entity.UserEntity;
import io.renren.entity.WithdrawalEntity;
import io.renren.service.CamilleService;
import io.renren.service.CommodityService;
import io.renren.service.OrderService;
import io.renren.service.PayConfigService;
import io.renren.service.UserService;
import io.renren.vo.CamilleVo;
import io.renren.vo.WithdrawalVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 卡密兑换请求接口
 *
 * @author tiechui
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="卡密兑换请求接口")
public class CamilleController {
    
    @Autowired
    private CamilleService camilleService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShareAsync shareAsync;
	
    
    /**
     * 卡密兑换接口
     * 
     * camilleNumber:卡密号(用户卡密平台购买)
     * 
     *
     * @author Mark sunlightcs@gmail.com
     * @throws ParseException 
     */
    @Login
    @PostMapping("CamilleExchange")
    @ApiOperation("用户卡密兑换")
    public Result<String> withdrawalflowpath(@LoginUser UserEntity user,@RequestBody CamilleVo camilleVo) throws ParseException{
    	//拿到用户ID
    	Long userId = user.getId();
    	//拿到用户输入的卡密号
    	String camilleNumber = camilleVo.getCamilleNumber();
    	//根据卡密号查询卡密详细信息
    	CamilleEntity camilleByNumber = camilleService.getCamilleByNumber(camilleNumber);
    	//拿到卡密状态
    	Integer status = camilleByNumber.getStatus();
    	//判断卡密是否可用
    	if(status == 1) {
    		
    		return new Result<String>().error("卡密已兑换,请输入未使用卡密!");
    		
    	}
    	//拿到卡密ID
    	Long camilleId = camilleByNumber.getId();
    	//拿到卡密名称
    	String camilleName = camilleByNumber.getCamilleName();
    	//拿到卡密规格
    	String camilleSpec = camilleByNumber.getCamilleSpec();
    	//拿到卡密价格
    	BigDecimal camillePrice = camilleByNumber.getCamillePrice();
    	//拿到卡密类型   类型(1:购买条数  2:购买VIP)
    	Integer type = camilleByNumber.getType();
    	
        /****************************订单添加记录*******************************************************/
    	//查询用户数据
    	UserEntity userEntity = userService.getUserByUserId(userId);
    	//封装订单数据
        OrderDTO orderDTO = new OrderDTO();
    	orderDTO.setUserId(userId);//充值用户ID
    	orderDTO.setUserName(userEntity.getNickName());//充值用户昵称
    	orderDTO.setCommodityId(camilleId);//充值商品ID
    	orderDTO.setCommodityName(camilleName);//充值商品名称
    	orderDTO.setCommodityType(type);//充值商品类型(1:购买条数  2:购买VIP)
    	orderDTO.setPayId(camilleId);//充值通道ID
    	orderDTO.setPayType(5);
    	//生成订单号
    	String orderCode = "KM_DH"+code();
    	orderDTO.setOrderCode(orderCode);//平台订单号
    	orderDTO.setTrilateralCode("");//三方支付订单号(查账订单号)
    	orderDTO.setRechargeAmount(camillePrice);//充值金额(元)
    	orderDTO.setOrderType(2);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
    	orderDTO.setFoundTime(new Date());//订单创建时间
    	orderDTO.setOperateTime(new Date());//订单操作时间
    	orderDTO.setOverTime(obtaindate());//订单超时时间 一般订单超时时间为3分钟  我们这里定义为30分钟
    	orderDTO.setCamilleSpec(camilleSpec);
    	orderService.save(orderDTO);
    	
    	/****************************订单添加成功后为用户添加购买的问答币和VIP*******************************************************/
    	//为用户加上购买的产品
        shareAsync.updateUserVIPorNumberCamille(orderDTO);
        
        //如果充值的是次数    异步为用户添加问答次数流水
        if(type == 1) {
            shareAsync.questionsFlowingWaterCamille(orderDTO);
        }
        
        //封装返佣订单数据
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);//用户ID
        orderEntity.setRechargeAmount(camillePrice);//充值金额
        orderEntity.setId(orderDTO.getId());//订单ID
        //异步为上级返佣(两级)
        shareAsync.InviteRebaterOne(orderEntity);
        
        //修改卡密状态
        camilleByNumber.setStatus(1);
        camilleService.updateById(camilleByNumber);
    	
    	
    	return new Result<String>().ok("卡密兑换成功!");
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

}