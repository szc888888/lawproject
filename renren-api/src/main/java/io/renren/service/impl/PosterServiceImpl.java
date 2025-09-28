package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.PosterDao;
import io.renren.entity.PosterEntity;
import io.renren.service.PosterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Service
public class PosterServiceImpl extends BaseServiceImpl<PosterDao, PosterEntity> implements PosterService {


    @Override
    public PosterEntity getOne() {
        return baseDao.getOne();
    }
}