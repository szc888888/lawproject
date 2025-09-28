package io.renren.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.dao.CamilleDao;
import io.renren.dto.CamilleDTO;
import io.renren.entity.CamilleEntity;
import io.renren.service.CamilleService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
@Service
public class CamilleServiceImpl extends CrudServiceImpl<CamilleDao, CamilleEntity, CamilleDTO> implements CamilleService {

	@Autowired
    private CamilleDao camilleDao;
	
    @Override
    public QueryWrapper<CamilleEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CamilleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

	@Override
	public CamilleEntity getCamilleByNumber(String camilleNumber) {
		
		CamilleEntity camilleByNumber = camilleDao.getCamilleByNumber(camilleNumber);
		
		return camilleByNumber;
	}


}