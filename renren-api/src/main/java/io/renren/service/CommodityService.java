package io.renren.service;


import java.util.List;
import java.util.Map;

import io.renren.common.service.CrudService;
import io.renren.dto.CommodityDTO;
import io.renren.entity.CommodityEntity;

/**
 * 商品Service
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
public interface CommodityService extends CrudService<CommodityEntity, CommodityDTO> {
	
	/**
	 * 根据条件查询商品
	 *
	 *
	 */
	List<CommodityEntity> getCommodityList(Map<String, Object> map);

}