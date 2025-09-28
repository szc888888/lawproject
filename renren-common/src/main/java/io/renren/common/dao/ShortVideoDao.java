package io.renren.common.dao;

import io.renren.common.dao.BaseDao;
import io.renren.common.entity.ShortVideoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短视频拉取数据
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-07-11
 */
@Mapper
public interface ShortVideoDao extends BaseDao<ShortVideoEntity> {
	
}