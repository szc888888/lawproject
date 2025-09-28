package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.common.utils.Result;
import io.renren.dao.TmplCategoryDao;
import io.renren.entity.AiModelEntity;
import io.renren.entity.TmplCategoryEntity;
import io.renren.service.TmplCategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class TmplCategoryServiceImpl extends BaseServiceImpl<TmplCategoryDao, TmplCategoryEntity> implements TmplCategoryService {


    @Override
    public Result queryTmplCate() {
        //查询所有后台设置的可用问题分类
        QueryWrapper<TmplCategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TmplCategoryEntity::getStatus, 0);
        wrapper.lambda().orderByDesc(TmplCategoryEntity::getCreateTime);
        return new Result().ok(baseDao.selectList(wrapper));
    }
}