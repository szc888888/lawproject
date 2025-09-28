package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.common.service.CrudService;
import io.renren.common.utils.Result;
import io.renren.entity.TmplCategoryEntity;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
public interface TmplCategoryService extends BaseService<TmplCategoryEntity> {

    Result queryTmplCate();

}