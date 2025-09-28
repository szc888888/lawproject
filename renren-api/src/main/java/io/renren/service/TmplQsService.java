package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.common.service.CrudService;
import io.renren.common.utils.Result;
import io.renren.entity.TmplQsEntity;

/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
public interface TmplQsService extends BaseService<TmplQsEntity> {

    Result queryQsByCateId(Long tid);

    Result queryAllQs();

}