package io.renren.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.dao.InviteRebateDao;
import io.renren.dto.InviteRebateDTO;
import io.renren.entity.InviteRebateEntity;
import io.renren.service.InviteRebateService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class InviteRebateServiceImpl extends CrudServiceImpl<InviteRebateDao, InviteRebateEntity, InviteRebateDTO> implements InviteRebateService {

	@Autowired
    private InviteRebateDao inviteRebateDao;
	
    @Override
    public QueryWrapper<InviteRebateEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<InviteRebateEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

	@Override
	public List<InviteRebateEntity> InviteRebateList(Map<String, Object> map) {
		
		List<InviteRebateEntity> inviteRebateList = inviteRebateDao.InviteRebateList(map);
		
		return inviteRebateList;
	}

	@Override
	public BigDecimal sumAcquireMoney(Map<String, Object> map) {
		
		BigDecimal sumAcquireMoney = inviteRebateDao.sumAcquireMoney(map);
		
		return sumAcquireMoney;
	}


}