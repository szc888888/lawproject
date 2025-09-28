package io.renren.modules.flagstudio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.flagstudio.dao.FlagstudioDao;
import io.renren.modules.flagstudio.dto.FlagstudioDTO;
import io.renren.modules.flagstudio.entity.FlagstudioEntity;
import io.renren.modules.flagstudio.service.FlagstudioService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Service
public class FlagstudioServiceImpl extends CrudServiceImpl<FlagstudioDao, FlagstudioEntity, FlagstudioDTO> implements FlagstudioService {

    @Override
    public QueryWrapper<FlagstudioEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<FlagstudioEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<FlagstudioEntity> queryAll() {
        QueryWrapper<FlagstudioEntity> wrapper = new QueryWrapper<>();
        return baseDao.selectList(wrapper);
    }
}