package io.renren.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.renren.common.service.CrudService;
import io.renren.dto.InviteRebateDTO;
import io.renren.entity.InviteRebateEntity;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
public interface InviteRebateService extends CrudService<InviteRebateEntity, InviteRebateDTO> {
	
	/**
	 * 根据用户ID查询用户邀请返利记录(100条)
	 *
	 *
	 */
	List<InviteRebateEntity> InviteRebateList(Map<String, Object> map);
	
	/**
	 * 根据用户ID查询用户所有提现成功的金额
	 *
	 *
	 */
	BigDecimal sumAcquireMoney(Map<String, Object> map);

}