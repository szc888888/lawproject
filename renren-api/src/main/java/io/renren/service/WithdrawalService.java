package io.renren.service;


import java.util.List;
import java.util.Map;

import io.renren.common.service.CrudService;
import io.renren.dto.WithdrawalDTO;
import io.renren.entity.WithdrawalEntity;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
public interface WithdrawalService extends CrudService<WithdrawalEntity, WithdrawalDTO> {
	
	/**
	 * 根据用户ID查询用户所有的提现记录
	 *
	 *
	 */
	List<WithdrawalEntity> getWithdrawalList(Map<String, Object> map);

}