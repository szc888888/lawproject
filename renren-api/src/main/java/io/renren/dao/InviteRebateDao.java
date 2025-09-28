package io.renren.dao;


import io.renren.common.dao.BaseDao;
import io.renren.entity.InviteRebateEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
@Mapper
public interface InviteRebateDao extends BaseDao<InviteRebateEntity> {
	
	/**
	 * 根据用户ID查询用户邀请返利记录(100条)
	 *
	 *
	 */
	List<InviteRebateEntity> InviteRebateList(Map<String, Object> map);
	
	/**
	 * 根据用户ID查询用户所有提现成功的金额
	 *
	 *
	 */
	BigDecimal sumAcquireMoney(Map<String, Object> map);
	
}