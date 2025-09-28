package io.renren.modules.sys.dao;


import io.renren.common.dao.BaseDao;
import io.renren.modules.sys.entity.OrderEntity;

import java.util.List;

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
	 * 查询所有支付中和订单创建的订单
	 * 
	 **/
	List<OrderEntity> getByOrderType();
	
}