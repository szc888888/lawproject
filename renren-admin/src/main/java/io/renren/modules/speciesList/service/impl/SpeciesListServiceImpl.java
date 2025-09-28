package io.renren.modules.speciesList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.speciesList.dao.SpeciesListDao;
import io.renren.modules.speciesList.dto.SpeciesListDTO;
import io.renren.modules.speciesList.entity.SpeciesListEntity;
import io.renren.modules.speciesList.service.SpeciesListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class SpeciesListServiceImpl extends CrudServiceImpl<SpeciesListDao, SpeciesListEntity, SpeciesListDTO> implements SpeciesListService {

    @Override
    public QueryWrapper<SpeciesListEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SpeciesListEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}