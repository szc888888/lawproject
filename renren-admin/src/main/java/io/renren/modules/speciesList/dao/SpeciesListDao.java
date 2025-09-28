package io.renren.modules.speciesList.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.speciesList.entity.SpeciesListEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface SpeciesListDao extends BaseDao<SpeciesListEntity> {
	
}