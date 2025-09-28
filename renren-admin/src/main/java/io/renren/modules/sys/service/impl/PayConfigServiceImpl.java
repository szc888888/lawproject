package io.renren.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.sys.dao.PayConfigDao;
import io.renren.modules.sys.dto.PayConfigDTO;
import io.renren.modules.sys.entity.PayConfigEntity;
import io.renren.modules.sys.service.PayConfigService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class PayConfigServiceImpl extends CrudServiceImpl<PayConfigDao, PayConfigEntity, PayConfigDTO> implements PayConfigService {

    @Override
    public QueryWrapper<PayConfigEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<PayConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}