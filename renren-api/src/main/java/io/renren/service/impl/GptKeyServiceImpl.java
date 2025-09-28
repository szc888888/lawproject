package io.renren.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.GptKeyDao;
import io.renren.entity.GptKeyEntity;
import io.renren.service.GptKeyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * KEY管理
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-12
 */
@Service
public class GptKeyServiceImpl extends BaseServiceImpl<GptKeyDao, GptKeyEntity> implements GptKeyService {




    @Override
    public List<String> getAllOpenKey() {
        return baseDao.getAllOpenKey();
    }

    @Override
    public List<String> getAllOpenKeyByType(int type) {
        return baseDao.getAllOpenKeyByType(type);
    }
}