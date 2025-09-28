package io.renren.modules.aimodel.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.aimodel.entity.AiModelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI角色模型表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-09
 */
@Mapper
public interface AiModelDao extends BaseDao<AiModelEntity> {
	
}