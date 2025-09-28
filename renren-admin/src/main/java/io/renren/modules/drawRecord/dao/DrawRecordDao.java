package io.renren.modules.drawRecord.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.drawRecord.entity.DrawRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Mapper
public interface DrawRecordDao extends BaseDao<DrawRecordEntity> {
	
}