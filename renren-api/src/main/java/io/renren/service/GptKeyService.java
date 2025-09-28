package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.common.service.CrudService;
import io.renren.entity.GptKeyEntity;

import java.util.List;

/**
 * KEY管理
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-12
 */
public interface GptKeyService extends BaseService<GptKeyEntity> {

    List<String> getAllOpenKey();

    List<String> getAllOpenKeyByType(int type);
}