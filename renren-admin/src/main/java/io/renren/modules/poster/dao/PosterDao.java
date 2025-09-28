package io.renren.modules.poster.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.poster.entity.PosterEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Mapper
public interface PosterDao extends BaseDao<PosterEntity> {

    PosterEntity getPoster();
}