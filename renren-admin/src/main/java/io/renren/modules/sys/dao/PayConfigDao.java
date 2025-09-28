package io.renren.modules.sys.dao;


import io.renren.common.dao.BaseDao;
import io.renren.modules.sys.entity.PayConfigEntity;

import org.apache.ibatis.annotations.Mapper;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface PayConfigDao extends BaseDao<PayConfigEntity> {
	
}