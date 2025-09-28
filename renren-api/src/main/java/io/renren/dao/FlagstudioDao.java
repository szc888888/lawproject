package io.renren.dao;

import io.renren.common.dao.BaseDao;
import io.renren.entity.FlagstudioEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Mapper
public interface FlagstudioDao extends BaseDao<FlagstudioEntity> {

    List<FlagstudioEntity> getListByTodayCountLt500();

}