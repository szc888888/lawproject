package io.renren.async;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.renren.dto.CommodityDTO;
import io.renren.dto.InviteRebateDTO;
import io.renren.dto.OrderDTO;
import io.renren.dto.PayConfigDTO;
import io.renren.entity.OrderEntity;
import io.renren.entity.PayConfigEntity;
import io.renren.entity.SpeciesListEntity;
import io.renren.entity.UserEntity;
import io.renren.service.CommodityService;
import io.renren.service.InviteRebateService;
import io.renren.service.OrderService;
import io.renren.service.PayConfigService;
import io.renren.service.SpeciesListService;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;

/**
 * 公用同步/异步类
 *
 * @author 
 */
@Component
public class ShareAsync {
    @Autowired
    private UserService userService;
    @Autowired
    private InviteRebateService inviteRebateService;
	@Resource
    private CommodityService commodityService;
	@Resource
    private OrderService orderService;
	@Autowired
	private SpeciesListService speciesListService;
    @Autowired
    private PayConfigService payConfigService;
    @Autowired
    private SysParamsService sysParamsService;
	
    /**
     * 异步为用户上级返佣(第一级)
     * 
     * pId:上级用户ID(当前充值用户直属上级)
     * commodityPrice:充值金额
     * orderId:当前订单ID
     * userId:当前充值用户ID
     * userName:当前充值用户名称
     *
     * @author 
     */
    @Async
    public void InviteRebaterOne(OrderEntity orderEntity) {
    	//拿到当前充值用户的ID
    	Long userId = orderEntity.getUserId();
    	//查询当前充值用户
    	UserEntity userByUserId = userService.getUserByUserId(orderEntity.getUserId());
    	//拿到用户上级
    	Long pId = userByUserId.getPid();
    	//拿到充值金额
    	BigDecimal commodityPrice = orderEntity.getRechargeAmount();
    	//当前充值用户名称
    	String userName = userByUserId.getNickName();
    	if(pId == null) {
    		
    		return;
    		
    	}
    	//根据ID查询用户数据
    	UserEntity user = userService.getUserByUserId(pId);
    	//拿到用户的可提现余额
    	BigDecimal money = user.getMoney();
    	//拿到一级返利百分比
    	BigDecimal leveloneProportion = user.getLeveloneProportion();
//    	String inviteMsgCount = paramsService.getValue("invite_rebate_one");
    	BigDecimal divide = leveloneProportion.divide(new BigDecimal("100"));
    	//计算上级能得到多少佣金
    	BigDecimal commission = commodityPrice.multiply(divide).setScale(2, RoundingMode.HALF_UP);
    	
    	BigDecimal moneyadd = money.add(commission);
    	
    	//修改用户可提现余额
    	UserEntity UserEntity = new UserEntity();
    	UserEntity.setMoney(moneyadd);
    	UserEntity.setId(pId);
    	userService.updateById(UserEntity);
    	
    	//为当前用户加上佣金流水
    	InviteRebateDTO inviteRebateDTO = new InviteRebateDTO();
    	inviteRebateDTO.setUserId(pId);//获得返利用户ID
    	inviteRebateDTO.setUserName(user.getNickName());//获得返利用户名称
    	inviteRebateDTO.setScale(leveloneProportion);//返利比例
    	inviteRebateDTO.setMoney(commodityPrice);//订单充值金额
    	inviteRebateDTO.setFrontVacancies(money);//返利前余额
    	inviteRebateDTO.setBehindVacancies(moneyadd);//获取佣金后余额(当前余额)
    	inviteRebateDTO.setAcquireMoney(commission);//获得金额
    	inviteRebateDTO.setBelowRank("下一级");//下级级别(下一级,下二级)
    	inviteRebateDTO.setBelowUserId(userId);//下级用户ID
    	inviteRebateDTO.setBelowUserName(userName);//下级用户名称
    	inviteRebateDTO.setOrderId(orderEntity.getId());//关联订单ID
    	inviteRebateDTO.setTime(new Date());//获取时间
    	inviteRebateService.save(inviteRebateDTO);
    	
    	//订单表添加返佣金额
    	OrderEntity neworderEntity = new OrderEntity();
    	neworderEntity.setId(orderEntity.getId());
    	neworderEntity.setCommission(commission.toString());
    	orderService.updateById(neworderEntity);
    	
    	//判断当前用户是否还有上级
    	Long pid2 = user.getPid();
    	if(pid2 != null) {
    		InviteRebaterTwo(pid2, commodityPrice,orderEntity.getId(),userId,userName,commission.toString());
    	}
	}
    
    
    /**
     * 异步为用户上级返佣(第二级)
     * 
     * pId:上级用户ID(当前充值用户二级上级)
     * commodityPrice:充值金额
     * orderId:当前订单ID
     * userId:当前充值用户ID
     * userName:当前充值用户名称
     * superior_commission:上级返利金额
     *
     * @author 
     */
    @Async
    public void InviteRebaterTwo(Long pId,BigDecimal commodityPrice,Long orderId,Long userId,String userName,String superior_commission) {
    	//根据ID查询用户数据
    	UserEntity user = userService.getUserByUserId(pId);
    	//拿到用户的可提现余额
    	BigDecimal money = user.getMoney();
    	//拿到二级返利百分比
    	BigDecimal secondaryProportion = user.getSecondaryProportion();
//    	String inviteMsgCount = paramsService.getValue("invite_rebate_two");
    	BigDecimal divide = secondaryProportion.divide(new BigDecimal("100"));
    	//计算上级能得到多少佣金
    	BigDecimal commission = commodityPrice.multiply(divide).setScale(2, RoundingMode.HALF_UP);
    	
    	BigDecimal moneyadd = money.add(commission);
    	
    	//修改用户可提现余额
    	UserEntity UserEntity = new UserEntity();
    	UserEntity.setMoney(moneyadd);
    	UserEntity.setId(pId);
    	userService.updateById(UserEntity);
    	
    	//为当前用户加上佣金流水
    	InviteRebateDTO inviteRebateDTO = new InviteRebateDTO();
    	inviteRebateDTO.setUserId(pId);//获得返利用户ID
    	inviteRebateDTO.setUserName(user.getNickName());//获得返利用户名称
    	inviteRebateDTO.setScale(secondaryProportion);//返利比例
    	inviteRebateDTO.setMoney(commodityPrice);//订单充值金额
    	inviteRebateDTO.setFrontVacancies(money);//返利前余额
    	inviteRebateDTO.setBehindVacancies(moneyadd);//获取佣金后余额(当前余额)
    	inviteRebateDTO.setAcquireMoney(commission);//获得金额
    	inviteRebateDTO.setBelowRank("下二级");//下级级别(下一级,下二级)
    	inviteRebateDTO.setBelowUserId(userId);//下级用户ID
    	inviteRebateDTO.setBelowUserName(userName);//下级用户名称
    	inviteRebateDTO.setOrderId(orderId);//关联订单ID
    	inviteRebateDTO.setTime(new Date());//获取时间
    	inviteRebateService.save(inviteRebateDTO);
    	
    	//订单表添加返佣金额
    	OrderEntity neworderEntity = new OrderEntity();
    	neworderEntity.setId(orderId);
    	neworderEntity.setCommission(superior_commission+","+commission.toString());
    	orderService.updateById(neworderEntity);
	}
    
    
    /**
     * 同步修改用户发送次数和VIP到期(正常支付)
     * 
     * userEntity:当前充值用户Entity
     * orderDTO:当前下单DTO
     * commodityDTO:商品详细信息
     *
     * @author 
     * @throws ParseException 
     * @throws NumberFormatException 
     */
    public void updateUserVIPorNumberOne(OrderEntity orderEntity) throws NumberFormatException, ParseException {
        //拿到用户ID
        Long userId = orderEntity.getUserId();
    	//根据用户ID查询用户数据
    	UserEntity userEntity = userService.getUserByUserId(userId);
    	//查询商品详细信息
    	CommodityDTO commodityDTO = commodityService.get(orderEntity.getCommodityId());
    	//拿到商品的规格  商品规格(条数/天数)
    	String commoditySpec = commodityDTO.getCommoditySpec();
    	//拿到用户充值类型  充值商品类型(1:购买条数  2:购买VIP)
    	Integer commodityType = orderEntity.getCommodityType();
    	

    	/****************************如果用户购买的是发送条数  那么直接为用户加上对应的条数*******************************************************/
    	if(commodityType == 1) {
    		//拿到用户的剩余条数
    		int msgCount = userEntity.getMsgCount();
    		
    		Integer gentle = Integer.parseInt(commoditySpec)+msgCount;
    		
    		//修改用户的发送条数
    		UserEntity newUserEntity = new UserEntity();
    		newUserEntity.setMsgCount(gentle);
    		newUserEntity.setId(userId);
    		userService.updateById(newUserEntity);
    	}
    	
    	/****************************如果用户购买的是VIP  那么将用户购买的时间加到VIP到期时间上面*******************************************************/
    	boolean vip = userEntity.isVip();
    	
    	if(commodityType == 2) {
    		
    		//用户还在VIP期限内
//    		if(vip) {
//    			//拿到用户的VIP到期时间
//    			Date vipDate = userEntity.getVipDate();
//    			//重新计算用户的VIP到期时间
//    			Date increasedate = increasedate(vipDate, Integer.parseInt(commoditySpec));
//    			//修改用户的到期时间
//        		UserEntity newUserEntity = new UserEntity();
//        		newUserEntity.setVipDate(increasedate);
//        		newUserEntity.setId(userId);
//        		userService.updateById(newUserEntity);
    			Date rechargeVip = userEntity.rechargeVip(Integer.parseInt(commoditySpec));
//    			//修改用户的到期时间
        		UserEntity newUserEntity = new UserEntity();
        		newUserEntity.setVipDate(rechargeVip);
        		newUserEntity.setId(userId);
        		userService.updateById(newUserEntity);
    			
        		
//        		//用户已经不在VIP期限内或者用户为第一次开通VIP
//    		}else {
//    			Date date = new Date();
//    			//计算用户的VIP到期时间
//    			Date increasedate = increasedate(date, Integer.parseInt(commoditySpec));
//    			//修改用户的VIP到期时间
//        		UserEntity newUserEntity = new UserEntity();
//        		newUserEntity.setVipDate(increasedate);
//        		newUserEntity.setId(userId);
//        		userService.updateById(newUserEntity);
//			}
    	}
    	
    	//用户购买合伙人商品
    	if(commodityType == 3) {
    		//拿到后台设置的合伙人一级比例
    		String invite_partner_one = sysParamsService.getValue("invite_partner_one");
    		//拿到后台设置的合伙人二级比例
    		String invite_partner_two = sysParamsService.getValue("invite_partner_two");
    		
//    		//用户还在VIP期限内
//    		if(vip) {
//    			//拿到用户的VIP到期时间
//    			Date vipDate = userEntity.getVipDate();
//    			//重新计算用户的VIP到期时间
//    			Date increasedate = increasedate(vipDate, Integer.parseInt(commoditySpec));
//    			//修改用户的到期时间
//        		UserEntity newUserEntity = new UserEntity();
//        		newUserEntity.setVipDate(increasedate);
//        		newUserEntity.setId(userId);
//        		userService.updateById(newUserEntity);
//        		
//        		//用户已经不在VIP期限内或者用户为第一次开通VIP
//    		}else {
//    			Date date = new Date();
//    			//计算用户的VIP到期时间
//    			Date increasedate = increasedate(date, Integer.parseInt(commoditySpec));
//    			//修改用户的VIP到期时间
//        		UserEntity newUserEntity = new UserEntity();
//        		newUserEntity.setVipDate(increasedate);
//        		newUserEntity.setId(userId);
//        		userService.updateById(newUserEntity);
//			}
    		
			Date rechargeVip = userEntity.rechargeVip(Integer.parseInt(commoditySpec));
////			//修改用户的到期时间
//    		UserEntity newUserEntity = new UserEntity();
//    		newUserEntity.setVipDate(rechargeVip);
//    		newUserEntity.setId(userId);
//    		userService.updateById(newUserEntity);
    		
    		//修改用户的返利比例
    		//修改用户的发送条数
    		UserEntity newUserEntity = new UserEntity();
    		newUserEntity.setLeveloneProportion(new BigDecimal(invite_partner_one));
    		newUserEntity.setSecondaryProportion(new BigDecimal(invite_partner_two));
    		newUserEntity.setVipDate(rechargeVip);
    		newUserEntity.setId(userId);
    		userService.updateById(newUserEntity);
    	}
	}
    
    
    /**
     * 同步修改用户发送次数和VIP到期(卡密购买)
     * 
     * userEntity:当前充值用户Entity
     * orderDTO:当前下单DTO
     * commodityDTO:商品详细信息
     *
     * @author 
     * @throws ParseException 
     * @throws NumberFormatException 
     */
    public void updateUserVIPorNumberCamille(OrderDTO orderDTO) throws NumberFormatException, ParseException {
        //拿到用户ID
        Long userId = orderDTO.getUserId();
    	//根据用户ID查询用户数据
    	UserEntity userEntity = userService.getUserByUserId(userId);
    	//查询商品详细信息
//    	CommodityDTO commodityDTO = commodityService.get(orderDTO.getCommodityId());
    	//拿到商品的规格  商品规格(条数/天数)
    	String camilleSpec = orderDTO.getCamilleSpec();
    	//拿到用户充值类型  充值商品类型(1:购买条数  2:购买VIP)
    	Integer commodityType = orderDTO.getCommodityType();
    	

    	/****************************如果用户购买的是发送条数  那么直接为用户加上对应的条数*******************************************************/
    	if(commodityType == 1) {
    		//拿到用户的剩余条数
    		int msgCount = userEntity.getMsgCount();
    		
    		Integer gentle = Integer.parseInt(camilleSpec)+msgCount;
    		
    		//修改用户的发送条数
    		UserEntity newUserEntity = new UserEntity();
    		newUserEntity.setMsgCount(gentle);
    		newUserEntity.setId(userId);
    		userService.updateById(newUserEntity);
    	}
    	
    	/****************************如果用户购买的是VIP  那么将用户购买的时间加到VIP到期时间上面*******************************************************/
    	boolean vip = userEntity.isVip();
    	
    	if(commodityType == 2) {
    		
    		//用户还在VIP期限内
    		if(vip) {
    			//拿到用户的VIP到期时间
    			Date vipDate = userEntity.getVipDate();
    			//重新计算用户的VIP到期时间
    			Date increasedate = increasedate(vipDate, Integer.parseInt(camilleSpec));
    			//修改用户的到期时间
        		UserEntity newUserEntity = new UserEntity();
        		newUserEntity.setVipDate(increasedate);
        		newUserEntity.setId(userId);
        		userService.updateById(newUserEntity);
        		
        		//用户已经不在VIP期限内或者用户为第一次开通VIP
    		}else {
    			Date date = new Date();
    			//计算用户的VIP到期时间
    			Date increasedate = increasedate(date, Integer.parseInt(camilleSpec));
    			//修改用户的VIP到期时间
        		UserEntity newUserEntity = new UserEntity();
        		newUserEntity.setVipDate(increasedate);
        		newUserEntity.setId(userId);
        		userService.updateById(newUserEntity);
			}
    	}
	}
    
    
    /**
     * 异步为用户添加用户问答流水(正常支付)
     * 
     * orderEntity:订单实体类
     * 
     *
     * @author 
     */
    @Async
    public void questionsFlowingWater(OrderEntity orderEntity) {
    	//拿到当前充值用户的ID
    	Long userId = orderEntity.getUserId();
    	//查询商品详细信息
    	CommodityDTO commodityDTO = commodityService.get(orderEntity.getCommodityId());
    	//拿到商品的规格  商品规格(条数/天数)
    	String commoditySpec = commodityDTO.getCommoditySpec();
    	
    	//增加一条提问次数流水记录
    	SpeciesListEntity speciesListEntity = new SpeciesListEntity();
    	speciesListEntity.setUserid(userId);//用户id
    	speciesListEntity.setSpecies(Integer.parseInt(commoditySpec));//聊天币数量
    	speciesListEntity.setCreateTime(new Date());//产生时间
    	speciesListEntity.setType(3);//类型(0问答1图片2平台赠送3充值4邀请奖励5签到赠送));
    	speciesListService.insert(speciesListEntity);
	}
    
    
    /**
     * 异步为用户添加用户问答流水(卡密购买)
     * 
     * orderEntity:订单实体类
     * 
     *
     * @author 
     */
    @Async
    public void questionsFlowingWaterCamille(OrderDTO orderDTO) {
    	//拿到当前充值用户的ID
    	Long userId = orderDTO.getUserId();
    	//拿到商品的规格  商品规格(条数/天数)
    	String commoditySpec = orderDTO.getCamilleSpec();
    	
    	//增加一条提问次数流水记录
    	SpeciesListEntity speciesListEntity = new SpeciesListEntity();
    	speciesListEntity.setUserid(userId);//用户id
    	speciesListEntity.setSpecies(Integer.parseInt(commoditySpec));//聊天币数量
    	speciesListEntity.setCreateTime(new Date());//产生时间
    	speciesListEntity.setType(3);//类型(0问答1图片2平台赠送3充值4邀请奖励5签到赠送));
    	speciesListService.insert(speciesListEntity);
	}
    
    
    /**
     * 异步修改通道的成功金额
     * 
     * orderEntity:订单实体类
     * 
     *
     * @author 
     */
    @Async
    public void amountSuccess(OrderEntity orderEntity) {
    	//拿到充值通道ID
    	Long payId = orderEntity.getPayId();
    	//拿到充值金额
    	BigDecimal rechargeAmount = orderEntity.getRechargeAmount();
    	
    	//根据通道ID查询通道数据
    	PayConfigDTO payConfigDTO = payConfigService.get(payId);
    	//拿到已成功支付的金额
    	BigDecimal amountSuccess = payConfigDTO.getAmountSuccess();
    	if(amountSuccess == null){
			amountSuccess = new BigDecimal(0);
		}
    	//计算成功金额
    	BigDecimal sumAmount = amountSuccess.add(rechargeAmount);
    	
    	PayConfigEntity payConfigEntity = new PayConfigEntity();
    	payConfigEntity.setAmountSuccess(sumAmount);
    	payConfigEntity.setId(payId);
    	payConfigService.updateById(payConfigEntity);
    	
	}
    
    
    /**
     * 
     * 加上指定天数后的时间
     * date:指定时间
     * fatalism:指定天数
     * @throws ParseException 
     * 
     * */
    public static Date increasedate(Date date,int fatalism) throws ParseException {
  	  SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  	  Calendar c = Calendar.getInstance();
  	  
  	  c.add(Calendar.DATE, fatalism);
  	  
  	  String str=dateFormat.format(c.getTime());
  	  date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
      return date;
  }
    
    

}
