package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.common.service.CrudService;
import io.renren.common.utils.Result;
import io.renren.entity.AiModelEntity;

/**
 * AI角色模型表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-09
 */
public interface AiModelService extends BaseService<AiModelEntity> {

    Result queryAiModel();

}