package io.renren.modules.sys.dao;


import io.renren.common.dao.BaseDao;
import io.renren.modules.sys.entity.WithdrawalEntity;

import org.apache.ibatis.annotations.Mapper;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Mapper
public interface WithdrawalDao extends BaseDao<WithdrawalEntity> {
	
}