package io.renren.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.dao.WithdrawalDao;
import io.renren.dto.WithdrawalDTO;
import io.renren.entity.WithdrawalEntity;
import io.renren.service.WithdrawalService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Service
public class WithdrawalServiceImpl extends CrudServiceImpl<WithdrawalDao, WithdrawalEntity, WithdrawalDTO> implements WithdrawalService {

	@Autowired
    private WithdrawalDao withdrawalDao;
	
    @Override
    public QueryWrapper<WithdrawalEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<WithdrawalEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

	@Override
	public List<WithdrawalEntity> getWithdrawalList(Map<String, Object> map) {

		List<WithdrawalEntity> withdrawalList = withdrawalDao.getWithdrawalList(map);
		
		return withdrawalList;
	}


}