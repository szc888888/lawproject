package io.renren.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.sys.dao.WithdrawalDao;
import io.renren.modules.sys.dto.WithdrawalDTO;
import io.renren.modules.sys.entity.WithdrawalEntity;
import io.renren.modules.sys.service.WithdrawalService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Service
public class WithdrawalServiceImpl extends CrudServiceImpl<WithdrawalDao, WithdrawalEntity, WithdrawalDTO> implements WithdrawalService {

    @Override
    public QueryWrapper<WithdrawalEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<WithdrawalEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.orderByDesc("time");

        return wrapper;
    }


}