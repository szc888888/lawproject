package io.renren.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.DrawRecordDao;
import io.renren.entity.DrawRecordEntity;
import io.renren.service.DrawRecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Service
public class DrawRecordServiceImpl extends BaseServiceImpl<DrawRecordDao, DrawRecordEntity> implements DrawRecordService {


    @Override
    public List<DrawRecordEntity> getListByUser(Long id) {
        return baseDao.getListByUser(id);
    }
}