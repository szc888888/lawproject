package io.renren.modules.tmplCategory.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.tmplCategory.dto.TmplCategoryDTO;
import io.renren.modules.tmplCategory.entity.TmplCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Mapper
public interface TmplCategoryDao extends BaseDao<TmplCategoryEntity> {

    List<TmplCategoryDTO> getAll();

}