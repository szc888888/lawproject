package io.renren.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.sys.dao.InviteRebateDao;
import io.renren.modules.sys.dto.InviteRebateDTO;
import io.renren.modules.sys.entity.InviteRebateEntity;
import io.renren.modules.sys.service.InviteRebateService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class InviteRebateServiceImpl extends CrudServiceImpl<InviteRebateDao, InviteRebateEntity, InviteRebateDTO> implements InviteRebateService {

    @Override
    public QueryWrapper<InviteRebateEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<InviteRebateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.orderByDesc("time");

        return wrapper;
    }


}