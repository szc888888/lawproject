package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.common.utils.Result;
import io.renren.entity.OrderEntity;
import io.renren.entity.PayConfigEntity;
import io.renren.entity.UserEntity;
import io.renren.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户订单请求接口
 *
 * @author tiechui
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="用户订单请求接口")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Login
    @GetMapping("OrderList")
    @ApiOperation("根据用户ID查询用户所有的充值订单")
    public Result<List<OrderEntity>> OrderList(@LoginUser UserEntity user){
    	//拿到用户ID
    	Long userId = user.getId();
    	
    	//根据条件查询用户的充值记录
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("orderType", 2);//订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
        
        List<OrderEntity> orderList = orderService.getOrderList(map);
        
    	
        return new Result<List<OrderEntity>>().ok(orderList);
    }
    
	/**
	 * 根据订单ID查询订单的支付状态
	 * 
	 * orderId:订单号
	 * 
	 * 返回:1(订单未支付或支付失败   2(订单支付成功)   3(订单号错误)
	 * 
	 * 
	 */
    @GetMapping("GetOrderById")
    @ApiOperation("根据订单ID查询订单的支付状态")
    public Result<Map<String, Object>> PayConfigList(String orderId){
    	
    	OrderEntity orderEntity = orderService.selectById(orderId);
    	
    	Map<String, Object> map = new HashMap<>();
    	
    	if(orderEntity == null) {
    		
    		map.put("type", 3);
    		
    		return new Result<Map<String, Object>>().ok(map);
    		
    	}
    	
    	//拿到订单状态
    	Integer orderType = orderEntity.getOrderType();
    	//判断订单是否成功
    	if(orderType == 2) {
    		
    		map.put("type", 2);
    		
    		return new Result<Map<String, Object>>().ok(map);
    		
    	}else {
    		
    		map.put("type", 1);
    		
    		return new Result<Map<String, Object>>().ok(map);
			
		}
    	
        
    }

}