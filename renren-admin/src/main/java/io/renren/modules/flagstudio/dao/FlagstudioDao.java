package io.renren.modules.flagstudio.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.flagstudio.entity.FlagstudioEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Mapper
public interface FlagstudioDao extends BaseDao<FlagstudioEntity> {
	
}