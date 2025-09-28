
package io.renren.modules.job.task;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import io.renren.modules.speciesList.entity.SpeciesListEntity;
import io.renren.modules.speciesList.service.SpeciesListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.modules.sys.service.SysParamsService;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.UserService;

/**
 * 会员每天更新可提问次数
 *
 * Task为spring bean的名称
 *
 * @author
 */
@Component("UserVIPMsgCount")
public class UserVIPMsgCount implements ITask{
	
	@Autowired
	private UserService userService;
	@Autowired
	private SysParamsService sysParamsService;
	@Autowired
	private SpeciesListService speciesListService;
	
	@Override
	public void run(String params){
		//查询出所有还没有到期的会员用户
		List<UserEntity> userList = userService.getUserList();
		//拿到配置里面会员每日赠送提问次数
		String value = sysParamsService.getValue("vip_gift_number");
		//循环判断用户的会员是否到期  如果没有到期那么就为用户加上提问次数
		for (UserEntity userEntity : userList) {
			//拿到会员到期时间
			if(userEntity.isVip()) {
				//拿到会员的提问次数
				int msgCount = userEntity.getMsgCount();
				//为用户加上每日赠送提问次数
				int msgCountSum = msgCount + Integer.parseInt(value);
				UserEntity newUserEntity = new UserEntity();
				newUserEntity.setMsgCount(msgCountSum);
				newUserEntity.setId(userEntity.getId());
				userService.updateById(newUserEntity);
				SpeciesListEntity speciesListEntity = new SpeciesListEntity();
				speciesListEntity.setSpecies(Integer.parseInt(value));
				speciesListEntity.setType(6);
				speciesListEntity.setCreateTime(new Date());
				speciesListEntity.setUserid(userEntity.getId());
				speciesListService.insert(speciesListEntity);
			}
		}
	}
}