
package io.renren.modules.job.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.renren.modules.sys.entity.OrderEntity;
import io.renren.modules.sys.service.OrderService;

/**
 * 支付订单超时过期定时任务
 *
 * Task为spring bean的名称
 *
 * @author
 */
@Component("OrderOverTime")
public class OrderOverTime implements ITask{
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public void run(String params){
		//查询所有支付中和订单创建的订单
		List<OrderEntity> orderEntity = orderService.getByOrderType();
		
		//循环List 做超时业务
		for (OrderEntity order : orderEntity) {
			//拿到订单的超时时间
			Date overTime = order.getOverTime();
			//判断当前时间是是否大于超时时间 如果结果为1  那么表示当前时间大于订单超时时间  结束该订单
			int compareTo = new Date().compareTo(overTime);
			if(compareTo == 1) {
				OrderEntity newOrderEntity = new OrderEntity();
				newOrderEntity.setId(order.getId());
				newOrderEntity.setOrderType(3);
				orderService.updateById(newOrderEntity);
			}
		}
	}
}