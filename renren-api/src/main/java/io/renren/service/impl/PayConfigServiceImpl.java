package io.renren.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.dao.PayConfigDao;
import io.renren.dto.PayConfigDTO;
import io.renren.entity.PayConfigEntity;
import io.renren.service.PayConfigService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Service
public class PayConfigServiceImpl extends CrudServiceImpl<PayConfigDao, PayConfigEntity, PayConfigDTO> implements PayConfigService {

	@Autowired
    private PayConfigDao payConfigDao;
	
    @Override
    public QueryWrapper<PayConfigEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<PayConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

	@Override
	public List<PayConfigEntity> getPayConfigList(Map<String, Object> map) {

		List<PayConfigEntity> payConfigList = payConfigDao.getPayConfigList(map);
		
		return payConfigList;
	}

	@Override
	public PayConfigEntity getPayConfigByType(Map<String, Object> map) {
		
		PayConfigEntity payConfigByType = payConfigDao.getPayConfigByType(map);
		
		return payConfigByType;
	}

	@Override
	public PayConfigEntity getPayConfigById(Map<String, Object> map) {

		PayConfigEntity getPayConfigById = payConfigDao.getPayConfigById(map);
		
		return getPayConfigById;
	}


}