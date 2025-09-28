package io.renren.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.dao.CommodityDao;
import io.renren.dto.CommodityDTO;
import io.renren.entity.CommodityEntity;
import io.renren.service.CommodityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品ServiceImpl
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Service
public class CommodityServiceImpl extends CrudServiceImpl<CommodityDao, CommodityEntity, CommodityDTO> implements CommodityService {
    
	@Autowired
    private CommodityDao commodityDao;

	@Override
	public List<CommodityEntity> getCommodityList(Map<String, Object> map) {
		
		List<CommodityEntity> commodityList = commodityDao.getCommodityList(map);
		
		return commodityList;
	}

	@Override
	public QueryWrapper<CommodityEntity> getWrapper(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}


}