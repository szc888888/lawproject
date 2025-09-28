package io.renren.service;

import java.util.List;
import java.util.Map;

import io.renren.common.service.BaseService;
import io.renren.entity.SpeciesListEntity;
import io.renren.entity.UserEntity;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
public interface SpeciesListService extends BaseService<SpeciesListEntity> {
	
	/**
	 * 根据传入条件查询用户的提问次数流水 
	 *
	 *
	 */
	List<SpeciesListEntity> getByUserIdSpeciesList(Map<String, Object> map);

	/**
	 * 查询用户问答次数流水 只返回最新300条
	 * @param user
	 * @return
	 */
	List<SpeciesListEntity> querySpeciesListByUserId(UserEntity user);
}