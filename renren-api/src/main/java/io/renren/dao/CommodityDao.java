package io.renren.dao;


import io.renren.common.dao.BaseDao;
import io.renren.entity.CommodityEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Dao
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Mapper
public interface CommodityDao extends BaseDao<CommodityEntity> {
	
	/**
	 * 根据条件查询商品
	 *
	 *
	 */
	List<CommodityEntity> getCommodityList(Map<String, Object> map);
	
}