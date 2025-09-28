package io.renren.service;


import io.renren.common.service.BaseService;
import io.renren.entity.FlagstudioEntity;

import java.util.List;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
public interface FlagstudioService extends BaseService<FlagstudioEntity> {

    List<FlagstudioEntity> getListByTodayCountLt500();


}