package io.renren.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.sys.dao.OrderDao;
import io.renren.modules.sys.dto.OrderDTO;
import io.renren.modules.sys.entity.OrderEntity;
import io.renren.modules.sys.service.OrderService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单表
 *
 * @author 
 * @since 1.0.0 2023-04-09
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

	@Autowired
	private OrderDao orderDao;
	
    @Override
    public QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.orderByDesc("found_time");
        return wrapper;
    }

	@Override
	public List<OrderEntity> getByOrderType() {
		
		List<OrderEntity> byOrderType = orderDao.getByOrderType();
		
		return byOrderType;
	}


}