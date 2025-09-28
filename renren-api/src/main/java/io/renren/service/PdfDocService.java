package io.renren.service;


import io.renren.common.service.BaseService;
import io.renren.common.utils.Result;
import io.renren.entity.PdfDocEntity;
import io.renren.entity.UserEntity;

import java.util.List;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
public interface PdfDocService extends BaseService<PdfDocEntity> {

    Result queryMyPdfList(UserEntity user);

    List<PdfDocEntity> queryPdfListByUserId(Long userId);
}