package io.renren.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.FlagstudioDao;
import io.renren.entity.FlagstudioEntity;
import io.renren.service.FlagstudioService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Service
public class FlagstudioServiceImpl extends BaseServiceImpl<FlagstudioDao, FlagstudioEntity> implements FlagstudioService {


    @Override
    public List<FlagstudioEntity> getListByTodayCountLt500() {
        return baseDao.getListByTodayCountLt500();
    }
}