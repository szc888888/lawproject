package io.renren.modules.aimodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.aimodel.dao.AiModelDao;
import io.renren.modules.aimodel.dto.AiModelDTO;
import io.renren.modules.aimodel.entity.AiModelEntity;
import io.renren.modules.aimodel.service.AiModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AI角色模型表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class AiModelServiceImpl extends CrudServiceImpl<AiModelDao, AiModelEntity, AiModelDTO> implements AiModelService {

    @Override
    public QueryWrapper<AiModelEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<AiModelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}