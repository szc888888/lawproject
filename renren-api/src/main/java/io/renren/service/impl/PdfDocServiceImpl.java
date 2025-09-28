package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.Result;
import io.renren.dao.PdfDocDao;
import io.renren.entity.PdfDocEntity;
import io.renren.entity.UserEntity;
import io.renren.service.PdfDocService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Service
public class PdfDocServiceImpl extends BaseServiceImpl<PdfDocDao, PdfDocEntity> implements PdfDocService {


    @Override
    public Result queryMyPdfList(UserEntity user) {
        QueryWrapper<PdfDocEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderByDesc("create_time");
        List<PdfDocEntity> pdfDocEntities = baseDao.selectList(wrapper);
        return new Result().ok(pdfDocEntities);
    }

    @Override
    public List<PdfDocEntity> queryPdfListByUserId(Long userId) {
        QueryWrapper<PdfDocEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<PdfDocEntity> pdfDocEntities = baseDao.selectList(wrapper);
        return pdfDocEntities;
    }
}