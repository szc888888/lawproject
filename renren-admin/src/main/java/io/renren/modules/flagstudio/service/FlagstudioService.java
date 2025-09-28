package io.renren.modules.flagstudio.service;

import io.renren.common.service.CrudService;
import io.renren.modules.flagstudio.dto.FlagstudioDTO;
import io.renren.modules.flagstudio.entity.FlagstudioEntity;

import java.util.List;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
public interface FlagstudioService extends CrudService<FlagstudioEntity, FlagstudioDTO> {

    List<FlagstudioEntity> queryAll();
}