package io.renren.modules.sys.dao;


import io.renren.common.dao.BaseDao;
import io.renren.modules.sys.entity.CommodityEntity;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Mapper
public interface CommodityDao extends BaseDao<CommodityEntity> {
	
	//查询所有的产品设置 
	List<CommodityEntity> getCommodityList();
	
}