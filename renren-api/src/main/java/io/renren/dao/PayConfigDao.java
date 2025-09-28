package io.renren.dao;


import io.renren.common.dao.BaseDao;
import io.renren.entity.PayConfigEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface PayConfigDao extends BaseDao<PayConfigEntity> {
	
	/**
	 * 根据条件查询所有可用的支付
	 *
	 *
	 */
	List<PayConfigEntity> getPayConfigList(Map<String, Object> map);



	/**
	 * 根据条件查询支付通道
	 *
	 *
	 */
	PayConfigEntity getPayConfigByType(Map<String, Object> map);
	
	
	/**
	 * 根据条件查询支付通道
	 *
	 *
	 */
	PayConfigEntity getPayConfigById(Map<String, Object> map);
	
}