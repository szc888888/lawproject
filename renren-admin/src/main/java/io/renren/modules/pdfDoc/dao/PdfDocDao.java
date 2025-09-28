package io.renren.modules.pdfDoc.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.pdfDoc.entity.PdfDocEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Mapper
public interface PdfDocDao extends BaseDao<PdfDocEntity> {
	
}