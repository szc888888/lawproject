package io.renren.modules.poster.service;

import io.renren.common.service.CrudService;
import io.renren.modules.poster.dto.PosterDTO;
import io.renren.modules.poster.entity.PosterEntity;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
public interface PosterService extends CrudService<PosterEntity, PosterDTO> {
    PosterEntity getPoster();

}