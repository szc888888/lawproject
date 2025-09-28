package io.renren.common.service;

import io.renren.common.dto.ShortVideoDTO;
import io.renren.common.entity.ShortVideoEntity;
import io.renren.common.service.CrudService;

import java.util.List;

/**
 * 短视频拉取数据
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-07-11
 */
public interface ShortVideoService extends CrudService<ShortVideoEntity, ShortVideoDTO> {

    List<ShortVideoEntity> getShortVideoListByUserId(Long id);
}