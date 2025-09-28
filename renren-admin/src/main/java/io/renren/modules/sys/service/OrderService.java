package io.renren.modules.sys.service;


import java.util.List;

import io.renren.common.service.CrudService;
import io.renren.modules.sys.dto.OrderDTO;
import io.renren.modules.sys.entity.OrderEntity;

/**
 * 订单表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {
	
	/**
	 * 查询所有支付中和订单创建的订单
	 * 
	 **/
	List<OrderEntity> getByOrderType();

}