package io.renren.modules.pdfDoc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.pdfDoc.dao.PdfDocDao;
import io.renren.modules.pdfDoc.dto.PdfDocDTO;
import io.renren.modules.pdfDoc.entity.PdfDocEntity;
import io.renren.modules.pdfDoc.service.PdfDocService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Service
public class PdfDocServiceImpl extends CrudServiceImpl<PdfDocDao, PdfDocEntity, PdfDocDTO> implements PdfDocService {

    @Override
    public QueryWrapper<PdfDocEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<PdfDocEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}