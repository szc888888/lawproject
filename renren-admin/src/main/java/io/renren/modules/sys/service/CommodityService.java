package io.renren.modules.sys.service;


import java.util.List;

import io.renren.common.service.CrudService;
import io.renren.modules.sys.dto.CommodityDTO;
import io.renren.modules.sys.entity.CommodityEntity;

/**
 * 商品表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
public interface CommodityService extends CrudService<CommodityEntity, CommodityDTO> {
	
	//查询所有的产品设置 
	List<CommodityEntity> getCommodityList();

}