package io.renren.service;


import java.util.List;
import java.util.Map;

import io.renren.common.service.CrudService;
import io.renren.dto.OrderDTO;
import io.renren.entity.OrderEntity;

/**
 * 订单表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {
	
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