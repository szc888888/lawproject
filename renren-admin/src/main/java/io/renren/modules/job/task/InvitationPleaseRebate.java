
package io.renren.modules.job.task;

import io.renren.modules.speciesList.service.SpeciesListService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.modules.sys.dto.InviteRebateDTO;
import io.renren.modules.sys.entity.CommodityEntity;
import io.renren.modules.sys.service.CommodityService;
import io.renren.modules.sys.service.InviteRebateService;
import io.renren.modules.sys.service.SysParamsService;
import io.renren.modules.user.dto.UserDTO;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.UserService;

/**
 * 根据用户ID为用户添加邀请返利条数
 *
 * Task为spring bean的名称
 *
 * @author
 */
@Component("InvitationPleaseRebate")
public class InvitationPleaseRebate implements ITask{
	
	@Autowired
	private UserService userService;
	@Autowired
	private InviteRebateService inviteRebateService;
    @Autowired
    private CommodityService commodityService;
	
	@Override
	public void run(String params){
		String[] results = params.split(",");
		if(params == null || params == "") {
			return;
		}
		for (int i = 0; i <  Integer.parseInt(results[1]); i++) {
			//根据参数查询用户   这个参数为需要生成邀请记录的userid
			UserDTO userDTO = userService.get(Long.valueOf(results[0]));
			//拿到所有的产品
			List<CommodityEntity> commodityList = commodityService.getCommodityList();
			//随机取出一个产品
			CommodityEntity commodityEntity = commodityList.get((int) (Math.random() * commodityList.size()));
			//拿到充值金额
			BigDecimal commodityPrice = commodityEntity.getCommodityPrice();
			//生成一个id
			String randomid = "166" +getRandomNickname(16);
			//随机生成一个用户名
			String nickname = "用户" +getRandomNickname(4);
	    	//拿到用户的可提现余额
	    	BigDecimal money = userDTO.getMoney();
	    	//拿到一级返利百分比
	    	BigDecimal leveloneProportion = userDTO.getLeveloneProportion();
	    	//拿到下二级的返利百分比
	    	BigDecimal secondaryProportion = userDTO.getSecondaryProportion();
	    	//获取一个随机数
		     List<String> list = Arrays.asList("1","2");
		     int index = (int) (Math.random()* list.size());
		     if(index == 1) {//一级返利
		    	 BigDecimal divide = leveloneProportion.divide(new BigDecimal("100"));
		     	//计算上级能得到多少佣金
		     	BigDecimal commission = commodityPrice.multiply(divide).setScale(2, RoundingMode.HALF_UP);
		     	BigDecimal moneyadd = money.add(commission);
		     	
		     	//修改用户可提现余额
		     	UserEntity UserEntity = new UserEntity();
		     	UserEntity.setMoney(moneyadd);
		     	UserEntity.setId(userDTO.getId());
		     	userService.updateById(UserEntity);
		     	
		     	//为当前用户加上佣金流水
		     	InviteRebateDTO inviteRebateDTO = new InviteRebateDTO();
		     	inviteRebateDTO.setUserId(userDTO.getId());//获得返利用户ID
		     	inviteRebateDTO.setUserName(userDTO.getNickName());//获得返利用户名称
		     	inviteRebateDTO.setScale(leveloneProportion);//返利比例
		     	inviteRebateDTO.setMoney(commodityPrice);//订单充值金额
		     	inviteRebateDTO.setFrontVacancies(money);//返利前余额
		     	inviteRebateDTO.setBehindVacancies(moneyadd);//获取佣金后余额(当前余额)
		     	inviteRebateDTO.setAcquireMoney(commission);//获得金额
		     	inviteRebateDTO.setBelowRank("下一级");//下级级别(下一级,下二级)
		     	inviteRebateDTO.setBelowUserId(Long.valueOf("166" +getRandomNickname(16)));//下级用户ID
		     	inviteRebateDTO.setBelowUserName(nickname);//下级用户名称
		     	inviteRebateDTO.setOrderId(Long.valueOf("166" +getRandomNickname(16)));//关联订单ID
		     	inviteRebateDTO.setTime(new Date());//获取时间
		     	inviteRebateService.save(inviteRebateDTO);
		     }else {//二级返利
		    	 BigDecimal divide = secondaryProportion.divide(new BigDecimal("100"));
		     	//计算上级能得到多少佣金
		     	BigDecimal commission = commodityPrice.multiply(divide).setScale(2, RoundingMode.HALF_UP);
		     	BigDecimal moneyadd = money.add(commission);
		     	
		     	//修改用户可提现余额
		     	UserEntity UserEntity = new UserEntity();
		     	UserEntity.setMoney(moneyadd);
		     	UserEntity.setId(userDTO.getId());
		     	userService.updateById(UserEntity);
		     	
		     	//为当前用户加上佣金流水
		     	InviteRebateDTO inviteRebateDTO = new InviteRebateDTO();
		     	inviteRebateDTO.setUserId(userDTO.getId());//获得返利用户ID
		     	inviteRebateDTO.setUserName(userDTO.getNickName());//获得返利用户名称
		     	inviteRebateDTO.setScale(secondaryProportion);//返利比例
		     	inviteRebateDTO.setMoney(commodityPrice);//订单充值金额
		     	inviteRebateDTO.setFrontVacancies(money);//返利前余额
		     	inviteRebateDTO.setBehindVacancies(moneyadd);//获取佣金后余额(当前余额)
		     	inviteRebateDTO.setAcquireMoney(commission);//获得金额
		     	inviteRebateDTO.setBelowRank("下二级");//下级级别(下一级,下二级)
		     	inviteRebateDTO.setBelowUserId(Long.valueOf("166" +getRandomNickname(16)));//下级用户ID
		     	inviteRebateDTO.setBelowUserName(nickname);//下级用户名称
		     	inviteRebateDTO.setOrderId(Long.valueOf("166" +getRandomNickname(16)));//关联订单ID
		     	inviteRebateDTO.setTime(new Date());//获取时间
		     	inviteRebateService.save(inviteRebateDTO);
			}
		}
		
    	
    	
    	
    	
    	
		
		
	}
	
	
	public static String getRandomNickname(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
		val += String.valueOf(random.nextInt(10));
		}
		return  val;
		}
		
		 public static void main(String[] args) {

		     List<String> list = Arrays.asList("1","2");
		     int index = (int) (Math.random()* list.size());
		     System.out.println(list.get(index));
		 }
		
}