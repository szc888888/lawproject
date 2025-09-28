package io.renren.dao;


import io.renren.common.dao.BaseDao;
import io.renren.entity.WithdrawalEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Mapper
public interface WithdrawalDao extends BaseDao<WithdrawalEntity> {
	
	/**
	 * 根据用户ID查询用户所有的提现记录
	 *
	 *
	 */
	List<WithdrawalEntity> getWithdrawalList(Map<String, Object> map);
	
}