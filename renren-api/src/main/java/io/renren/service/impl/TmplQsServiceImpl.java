package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.Result;
import io.renren.dao.TmplQsDao;
import io.renren.entity.TmplCategoryEntity;
import io.renren.entity.TmplQsEntity;
import io.renren.service.TmplQsService;
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
public class TmplQsServiceImpl extends BaseServiceImpl<TmplQsDao, TmplQsEntity> implements TmplQsService {


    @Override
    public Result queryQsByCateId(Long tid) {
        //根据分类ID查询问题列表
        QueryWrapper<TmplQsEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TmplQsEntity::getStatus, 0);
        wrapper.lambda().eq(TmplQsEntity::getTid, tid);
        wrapper.lambda().orderByDesc(TmplQsEntity::getCreateTime);
        return new Result().ok(baseDao.selectList(wrapper));
    }

    @Override
    public Result queryAllQs() {
        QueryWrapper<TmplQsEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TmplQsEntity::getStatus, 0);
        wrapper.lambda().orderByDesc(TmplQsEntity::getCreateTime);
        return new Result().ok(baseDao.selectList(wrapper));
    }
}