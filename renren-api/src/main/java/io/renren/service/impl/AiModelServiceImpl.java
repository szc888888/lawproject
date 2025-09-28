package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.Result;
import io.renren.dao.AiModelDao;
import io.renren.entity.AiModelEntity;
import io.renren.service.AiModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * AI角色模型表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class AiModelServiceImpl extends BaseServiceImpl<AiModelDao, AiModelEntity> implements AiModelService {


    @Override
    public Result queryAiModel() {
        //查询所有后台设置的可用模型
        QueryWrapper<AiModelEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(AiModelEntity::getStatus, 0);
        wrapper.lambda().orderByDesc(AiModelEntity::getCreateTime);
        return new Result().ok(baseDao.selectList(wrapper));
    }
}