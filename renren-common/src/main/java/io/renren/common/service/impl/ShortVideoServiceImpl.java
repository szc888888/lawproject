package io.renren.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.dao.ShortVideoDao;
import io.renren.common.dto.ShortVideoDTO;
import io.renren.common.entity.ShortVideoEntity;
import io.renren.common.service.ShortVideoService;
import io.renren.common.service.impl.CrudServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 短视频拉取数据
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-07-11
 */
@Service
public class ShortVideoServiceImpl extends CrudServiceImpl<ShortVideoDao, ShortVideoEntity, ShortVideoDTO> implements ShortVideoService {

    @Override
    public QueryWrapper<ShortVideoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ShortVideoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<ShortVideoEntity> getShortVideoListByUserId(Long id) {
        QueryWrapper<ShortVideoEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ShortVideoEntity::getUserId,id);
        return baseDao.selectList(wrapper);
    }
}