package io.renren.modules.poster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.poster.dao.PosterDao;
import io.renren.modules.poster.dto.PosterDTO;
import io.renren.modules.poster.entity.PosterEntity;
import io.renren.modules.poster.service.PosterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Service
public class PosterServiceImpl extends CrudServiceImpl<PosterDao, PosterEntity, PosterDTO> implements PosterService {

    @Override
    public QueryWrapper<PosterEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<PosterEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }
    public PosterEntity getPoster() {
        return baseDao.getPoster();
    }

}