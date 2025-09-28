package io.renren.dao;


import io.renren.common.dao.BaseDao;
import io.renren.entity.OrderEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {
	
	/**
	 * 根据传入条件查询用户充值记录
	 *
	 *
	 */
	List<OrderEntity> getOrderList(Map<String, Object> map);
	
	
	/**
	 * 根据订单号查询订单数据
	 *
	 *
	 */
	OrderEntity getOrderByCode(String orderCode);
	
}