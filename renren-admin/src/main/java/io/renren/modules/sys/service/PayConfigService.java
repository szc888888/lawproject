package io.renren.modules.sys.service;


import io.renren.common.service.CrudService;
import io.renren.modules.sys.dto.PayConfigDTO;
import io.renren.modules.sys.entity.PayConfigEntity;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
public interface PayConfigService extends CrudService<PayConfigEntity, PayConfigDTO> {

}