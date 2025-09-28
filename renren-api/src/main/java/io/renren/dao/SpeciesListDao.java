package io.renren.dao;

import io.renren.common.dao.BaseDao;
import io.renren.entity.SpeciesListEntity;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface SpeciesListDao extends BaseDao<SpeciesListEntity> {
	
	/**
	 * 根据传入条件查询用户的提问次数流水 
	 *
	 *
	 */
	List<SpeciesListEntity> getByUserIdSpeciesList(Map<String, Object> map);

	List<SpeciesListEntity> querySpeciesListByUserId(Long id);
}