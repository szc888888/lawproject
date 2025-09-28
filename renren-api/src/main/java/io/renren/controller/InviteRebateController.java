package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.common.utils.Result;
import io.renren.entity.InviteRebateEntity;
import io.renren.entity.UserEntity;
import io.renren.service.InviteRebateService;
import io.renren.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户邀请返利记录请求接口
 *
 * @author tiechui
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="用户邀请返利记录请求接口")
public class InviteRebateController {
    
    @Autowired
    private InviteRebateService inviteRebateService;
    @Autowired
	private UserService userService;
    
    @Login
    @GetMapping("InviteRebateList")
    @ApiOperation("根据用户ID查询用户邀请返利记录(100条)")
    public Result<Map<String, Object>> OrderList(@LoginUser UserEntity user){
    	//拿到登录用户的ID
    	Long userId = user.getId();
    	
    	//拿到用户的提现总额
    	BigDecimal money = user.getMoney();
    	
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        
    	//根据用户ID查询用户邀请返利记录(100条)
    	List<InviteRebateEntity> inviteRebateList = inviteRebateService.InviteRebateList(map);
    	
    	//查询用户所有提现成功的总额
    	BigDecimal sumAcquireMoney = inviteRebateService.sumAcquireMoney(map);
    	if(sumAcquireMoney == null) {
    		sumAcquireMoney = new BigDecimal(0);
    	}
    	
    	//计算用户的总收益
//    	BigDecimal TotalRevenue = sumAcquireMoney.add(money);
    	//查询我的直属邀请人数
		int userCount = userService.countUserByPid(user.getId());
    	Map<String, Object> returnmap = new HashMap<>();
    	returnmap.put("inviteRebateList", inviteRebateList);
    	returnmap.put("TotalRevenue", sumAcquireMoney);
    	returnmap.put("money", money);
    	returnmap.put("userCount", userCount);

        return new Result<Map<String, Object>>().ok(returnmap);
    }
	@Login
	@GetMapping("sonList")
	@ApiOperation("查询下级人数列表,暂时未做分页 后期看情况加上")
	public Result<Map<String, Object>> sonList(@LoginUser UserEntity user){
		//查询我的直属邀请人数
		int userCount = userService.countUserByPid(user.getId());
		//查询我二级下级人数
		int twoUserCount = userService.countUserByGpid(user.getId());
		//查询直属下级列表
		List<UserEntity> list = userService.getUserListByPid(user.getId());
		Map<String, Object> returnmap = new HashMap<>();
		returnmap.put("userCount", userCount);
		returnmap.put("twoUserCount", twoUserCount);
		returnmap.put("userList", list);
		returnmap.put("leveloneProportion", user.getLeveloneProportion());
		returnmap.put("secondaryProportion", user.getSecondaryProportion());
		return new Result<Map<String, Object>>().ok(returnmap);
	}
}