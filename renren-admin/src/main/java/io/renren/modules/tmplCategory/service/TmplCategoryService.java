package io.renren.modules.tmplCategory.service;

import io.renren.common.service.CrudService;
import io.renren.modules.tmplCategory.dto.TmplCategoryDTO;
import io.renren.modules.tmplCategory.entity.TmplCategoryEntity;

import java.util.List;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
public interface TmplCategoryService extends CrudService<TmplCategoryEntity, TmplCategoryDTO> {

    List<TmplCategoryDTO> getAll();

}