package io.renren.dao;

import io.renren.common.dao.BaseDao;
import io.renren.entity.DrawRecordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Mapper
public interface DrawRecordDao extends BaseDao<DrawRecordEntity> {

    List<DrawRecordEntity> getListByUser(Long id);
}