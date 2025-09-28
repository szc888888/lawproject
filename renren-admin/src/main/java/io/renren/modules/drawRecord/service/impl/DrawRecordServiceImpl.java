package io.renren.modules.drawRecord.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.drawRecord.dao.DrawRecordDao;
import io.renren.modules.drawRecord.dto.DrawRecordDTO;
import io.renren.modules.drawRecord.entity.DrawRecordEntity;
import io.renren.modules.drawRecord.service.DrawRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Service
public class DrawRecordServiceImpl extends CrudServiceImpl<DrawRecordDao, DrawRecordEntity, DrawRecordDTO> implements DrawRecordService {

    @Override
    public QueryWrapper<DrawRecordEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<DrawRecordEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}