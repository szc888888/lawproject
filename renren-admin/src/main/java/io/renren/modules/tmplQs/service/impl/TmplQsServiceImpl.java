package io.renren.modules.tmplQs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.tmplQs.dao.TmplQsDao;
import io.renren.modules.tmplQs.dto.TmplQsDTO;
import io.renren.modules.tmplQs.entity.TmplQsEntity;
import io.renren.modules.tmplQs.service.TmplQsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class TmplQsServiceImpl extends CrudServiceImpl<TmplQsDao, TmplQsEntity, TmplQsDTO> implements TmplQsService {

    @Override
    public QueryWrapper<TmplQsEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<TmplQsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}