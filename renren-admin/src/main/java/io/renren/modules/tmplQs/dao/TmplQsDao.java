package io.renren.modules.tmplQs.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.tmplQs.entity.TmplQsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Mapper
public interface TmplQsDao extends BaseDao<TmplQsEntity> {
	
}