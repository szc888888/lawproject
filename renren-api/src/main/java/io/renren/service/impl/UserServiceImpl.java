/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;

import cn.hutool.core.util.StrUtil;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.dao.UserDao;
import io.renren.entity.TokenEntity;
import io.renren.entity.UserEntity;
import io.renren.dto.LoginDTO;
import io.renren.service.QuestionAnswerService;
import io.renren.service.SysParamsService;
import io.renren.service.TokenService;
import io.renren.service.UserService;
import io.renren.vo.MsgMoneyVo;
import io.renren.vo.TodayQsCount;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, UserEntity> implements UserService {
	@Autowired
	private TokenService tokenService;
	@Autowired
	private QuestionAnswerService questionAnswerService;
	@Autowired
	private SysParamsService sysParamsService;

	@Override
	public UserEntity getByMobile(String mobile) {
		return baseDao.getUserByMobile(mobile);
	}

	@Override
	public UserEntity getUserByUserId(Long userId) {
		return baseDao.getUserByUserId(userId);
	}

	@Override
	public Map<String, Object> login(LoginDTO dto) {
		UserEntity user = getByMobile(dto.getMobile());
		AssertUtils.isNull(user, ErrorCode.ACCOUNT_PASSWORD_ERROR);

		//密码错误
		if(!user.getPassword().equals(DigestUtils.sha256Hex(dto.getPassword()))){
			throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
		}

		//获取登录token
		TokenEntity tokenEntity = tokenService.createToken(user.getId());

		Map<String, Object> map = new HashMap<>(2);
		map.put("token", tokenEntity.getToken());
		map.put("expire", tokenEntity.getExpireDate().getTime() - System.currentTimeMillis());

		return map;
	}

	@Override
	public void updateMsgCount(Long userId, int count) {
		Map<String,Object> map = new HashMap<>();
		map.put("userId",userId);
		map.put("msgCount",count);
		baseDao.updateMsgCount(map);
	}

	@Override
	public UserEntity getUserByOpenId(String openid) {
		return baseDao.getUserByOpenId(openid);
	}

	@Override
	public String creatToken(UserEntity user) {
		//获取登录token
		TokenEntity tokenEntity = tokenService.createToken(user.getId());

		return tokenEntity.getToken();
	}

	@Override
	public UserEntity getUserByUnionId(String unionId) {
		return baseDao.getUserByUnionId(unionId);
	}

	@Override
	public void updateUnionidAndOpenId(UserEntity user) {
		UserEntity nUserEntity = new UserEntity();
		nUserEntity.setId(user.getId());
		nUserEntity.setWxUnionid(user.getWxUnionid());
		nUserEntity.setWxOpenid(user.getWxOpenid());
		nUserEntity.setMobile(user.getMobile());
		updateById(nUserEntity);
	}

	@Override
	public void updateMobile(UserEntity user) {
		UserEntity nUserEntity = new UserEntity();
		nUserEntity.setId(user.getId());
		nUserEntity.setMobile(user.getMobile());
		nUserEntity.setUsername(user.getUsername());
		updateById(nUserEntity);
	}

	@Override
	public int countUserByPid(Long id) {
		return baseDao.countUserByPid(id);
	}

	@Override
	public int countUserByGpid(Long id) {
		return baseDao.countUserByGpid(id);
	}

	@Override
	public List<UserEntity> getUserListByPid(Long id) {
		return baseDao.getUserListByPid(id);
	}

	/**
	 * 用户是否允许问答 仅仅快速问答处使用
	 * @param userEntity  用户
	 * @param type 0是3.5 1是4.0
	 * @return
	 */
	@Override
	public MsgMoneyVo isMsg(UserEntity userEntity, int type) {
		MsgMoneyVo msgMoneyVo = new MsgMoneyVo();
		msgMoneyVo.setMsgOk(true);//默认是当他有每日免费次数的 可以发消息
		msgMoneyVo.setCharging(false);//默认他有每日免费次数的 可以发消息并且不扣钱
		msgMoneyVo.setType(type);
		//查询今日用户3.5和4.0的问答消息次数
		TodayQsCount todayQsCount = questionAnswerService.count3And4TodayByUser(userEntity.getId());
		int threeCuont = todayQsCount.getThreeCuont();
		int fourCuont = todayQsCount.getFourCuont();
		int count3 = 0;
		int count4 = 0;
		int msgMoney = 0;//一条信息多少钱
		//查询后台设置的 3.5 4.0 普通用户 会员用户每日免费条数
		if(userEntity.isVip()){
			String COUNT_VIP_3 = sysParamsService.getValue("COUNT_VIP_3");//3.5VIP用户每日免费次数
			if(StrUtil.isNotBlank(COUNT_VIP_3)){
				count3 = Integer.parseInt(COUNT_VIP_3);
			}
			String COUNT_VIP_4 = sysParamsService.getValue("COUNT_VIP_4");//4.0VIP用户每日免费次数
			if(StrUtil.isNotBlank(COUNT_VIP_4)){
				count4 = Integer.parseInt(COUNT_VIP_4);
			}
		}else{
			String COUNT_USER_3 = sysParamsService.getValue("COUNT_USER_3");//3.5普通用户每日免费次数
			if(StrUtil.isNotBlank(COUNT_USER_3)){
				count3 = Integer.parseInt(COUNT_USER_3);
			}
			String COUNT_USER_4 = sysParamsService.getValue("COUNT_USER_4");//4.0普通用户每日免费次数
			if(StrUtil.isNotBlank(COUNT_USER_4)){
				count4 = Integer.parseInt(COUNT_USER_4);
			}
		}
		if(type == 0){
			//判断3.5价钱
			String MONEY3 = sysParamsService.getValue("MONEY3");//3.5问答单价
			if(StrUtil.isBlank(MONEY3)){
				MONEY3 = "1";
			}
			msgMoney = Integer.parseInt(MONEY3);
			if(threeCuont < count3){//今日免费问答次数没有超过设置的
				return msgMoneyVo;
			}
		}else{
			//判断4.0
			String MONEY4 = sysParamsService.getValue("MONEY4");//4.0问答单价
			if(StrUtil.isBlank(MONEY4)){
				MONEY4 = "5";
			}
			msgMoney = Integer.parseInt(MONEY4);
			if(fourCuont < count4){//今日免费问答次数没有超过设置的
				return msgMoneyVo;
			}
		}
		//超过了 那就判断金额够不够扣 没超过上面就已经返回了 不会到这里
		if(userEntity.getMsgCount() < msgMoney){//这里余额不足了
			msgMoneyVo.setMsgOk(false);
			return msgMoneyVo;
		}
		//到这里了 就是余额足的  但是每日免费额度用完啦
		msgMoneyVo.setMoney(msgMoney);//这里就是扣费金额
		msgMoneyVo.setCharging(true);//走到这里了 说明每天免费次数用完了  就要扣费了
		return msgMoneyVo;
	}

}