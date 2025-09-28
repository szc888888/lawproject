package io.renren.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
@Data
@ApiModel(value = "邀请返利表")
public class InviteRebateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "邀请返利ID(主键ID)")
	private Long id;

	@ApiModelProperty(value = "获得返利用户ID")
	private Long userId;

	@ApiModelProperty(value = "获得返利用户名称")
	private String userName;

	@ApiModelProperty(value = "返利比例")
	private BigDecimal scale;
	
	@ApiModelProperty(value = "返利前余额")
	private BigDecimal frontVacancies;
	
	@ApiModelProperty(value = "获取佣金后余额(当前余额)")
	private BigDecimal behindVacancies;

	@ApiModelProperty(value = "订单充值金额")
	private BigDecimal money;

	@ApiModelProperty(value = "获得金额")
	private BigDecimal acquireMoney;

	@ApiModelProperty(value = "下级级别(下一级,下二级)")
	private String belowRank;

	@ApiModelProperty(value = "下级用户ID")
	private Long belowUserId;

	@ApiModelProperty(value = "下级用户名称")
	private String belowUserName;

	@ApiModelProperty(value = "关联订单ID")
	private Long orderId;

	@ApiModelProperty(value = "创建时间")
	private Date time;


}