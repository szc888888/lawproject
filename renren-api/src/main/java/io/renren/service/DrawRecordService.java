package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.entity.DrawRecordEntity;

import java.util.List;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
public interface DrawRecordService extends BaseService<DrawRecordEntity> {

    List<DrawRecordEntity> getListByUser(Long id);
}