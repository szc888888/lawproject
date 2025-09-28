package io.renren.service;


import io.renren.common.service.CrudService;
import io.renren.dto.CamilleDTO;
import io.renren.entity.CamilleEntity;

/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
public interface CamilleService extends CrudService<CamilleEntity, CamilleDTO> {
	
	/**
	 * 根据卡密号拆查询卡密信息
	 *
	 *
	 */
	CamilleEntity getCamilleByNumber(String camilleNumber);

}