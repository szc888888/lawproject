package io.renren.modules.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.sys.dao.CommodityDao;
import io.renren.modules.sys.dto.CommodityDTO;
import io.renren.modules.sys.entity.CommodityEntity;
import io.renren.modules.sys.service.CommodityService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Service
public class CommodityServiceImpl extends CrudServiceImpl<CommodityDao, CommodityEntity, CommodityDTO> implements CommodityService {
	@Autowired
	private CommodityDao commodityDao;
	
    @Override
    public QueryWrapper<CommodityEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CommodityEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

	@Override
	public List<CommodityEntity> getCommodityList() {
		
		List<CommodityEntity> commodityList = commodityDao.getCommodityList();
		
		return commodityList;
	}


}