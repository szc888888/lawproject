package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.cf.annotation.PreventRepeat;
import io.renren.common.utils.Result;
import io.renren.entity.SpeciesListEntity;
import io.renren.entity.UserEntity;
import io.renren.service.SpeciesListService;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户订单请求接口
 *
 * @author tiechui
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="用户签到请求接口")
public class SignInController {
    
    @Autowired
    private UserService userService;
	@Autowired
	private SysParamsService sysParamsService;
	@Autowired
	private SpeciesListService speciesListService;
	
    @Login
    @GetMapping("SignIn")
    @ApiOperation("签到")
	@PreventRepeat
    public Result<String> OrderList(@LoginUser UserEntity user){
    	//拿到用户ID
    	Long userId = user.getId();
    	
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
    	//查询用户今天是否已经签到
    	List<SpeciesListEntity> byUserIdSpeciesList = speciesListService.getByUserIdSpeciesList(map);
    	if(byUserIdSpeciesList.size() != 0) {
    		return new Result<String>().error("你已签到!");
    	}
    	
    	//查询每日签到赠送次数
    	String value = sysParamsService.getValue("sign_in");
    	//为用户加上签到次数
    	int msgCount = user.getMsgCount() + Integer.parseInt(value);
    	//修改用户数据
    	UserEntity UserEntity = new UserEntity();
    	UserEntity.setMsgCount(msgCount);
    	UserEntity.setId(userId);
    	userService.updateById(UserEntity);
    	
    	//增加一条提问次数流水记录
    	SpeciesListEntity speciesListEntity = new SpeciesListEntity();
    	speciesListEntity.setUserid(userId);//用户id
    	speciesListEntity.setSpecies(Integer.parseInt(value));//聊天币数量
    	speciesListEntity.setCreateTime(new Date());//产生时间
    	speciesListEntity.setType(5);//类型(0问答1图片2平台赠送3充值4邀请奖励5签到赠送));
    	speciesListService.insert(speciesListEntity);
        return new Result<String>().ok("签到成功!");
    }

	@Login
	@GetMapping("isSign")
	@ApiOperation("查询是否签到")
	public Result isSign(@LoginUser UserEntity user){
		//拿到用户ID
		Long userId = user.getId();

		Map<String, Object> map = new HashMap<>();
		String value = sysParamsService.getValue("sign_in");
		map.put("userId", userId);
		map.put("msgCount", value);//签到赠送次数
		//查询用户今天是否已经签到
		List<SpeciesListEntity> byUserIdSpeciesList = speciesListService.getByUserIdSpeciesList(map);
		map.put("type","0");
		if(byUserIdSpeciesList.size() != 0) {
			map.put("type","1");
			return new Result<>().ok(map);
		}

		return new Result<>().ok(map);
	}
}