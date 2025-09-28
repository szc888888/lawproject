package io.renren.service.impl;


import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.SpeciesListDao;
import io.renren.entity.SpeciesListEntity;
import io.renren.entity.UserEntity;
import io.renren.service.SpeciesListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class SpeciesListServiceImpl extends BaseServiceImpl<SpeciesListDao, SpeciesListEntity> implements SpeciesListService {

	@Autowired
    private SpeciesListDao seciesListDao;
	
	@Override
	public List<SpeciesListEntity> getByUserIdSpeciesList(Map<String, Object> map) {
		
		List<SpeciesListEntity> byUserIdSpeciesList = seciesListDao.getByUserIdSpeciesList(map);
		
		return byUserIdSpeciesList;
	}

	@Override
	public List<SpeciesListEntity> querySpeciesListByUserId(UserEntity user) {
		return seciesListDao.querySpeciesListByUserId(user.getId());
	}


}