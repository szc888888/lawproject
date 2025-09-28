package io.renren.modules.tmplCategory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.tmplCategory.dao.TmplCategoryDao;
import io.renren.modules.tmplCategory.dto.TmplCategoryDTO;
import io.renren.modules.tmplCategory.entity.TmplCategoryEntity;
import io.renren.modules.tmplCategory.service.TmplCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class TmplCategoryServiceImpl extends CrudServiceImpl<TmplCategoryDao, TmplCategoryEntity, TmplCategoryDTO> implements TmplCategoryService {

    @Override
    public QueryWrapper<TmplCategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<TmplCategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<TmplCategoryDTO> getAll() {

        return baseDao.getAll();
    }
}