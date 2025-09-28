package io.renren.dao;



import io.renren.common.dao.BaseDao;
import io.renren.entity.CamilleEntity;


import org.apache.ibatis.annotations.Mapper;

/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
@Mapper
public interface CamilleDao extends BaseDao<CamilleEntity> {
	
	/**
	 * 根据卡密号拆查询卡密信息
	 *
	 *
	 */
	CamilleEntity getCamilleByNumber(String camilleNumber);
	
}