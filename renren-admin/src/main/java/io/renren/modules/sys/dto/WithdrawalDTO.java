package io.renren.modules.sys.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Data
@ApiModel(value = "用户提现表")
public class WithdrawalDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "提现表ID（主键ID）")
	private Long id;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "提现用户昵称")
	private String userName;

	@ApiModelProperty(value = "提现金额")
	private BigDecimal withdrawalMoney;

	@ApiModelProperty(value = "实到金额(扣除手续费后金额)")
	private BigDecimal realityMoney;

	@ApiModelProperty(value = "提现前余额")
	private BigDecimal frontVacancies;

	@ApiModelProperty(value = "提现后余额(当前余额)")
	private BigDecimal behindVacancies;

	@ApiModelProperty(value = "提现手续费")
	private BigDecimal serviceCharge;

	@ApiModelProperty(value = "提现手续费比例")
	private BigDecimal serviceChargeScale;

	@ApiModelProperty(value = "用户收款二维码链接")
	private String makeCollectionsUrl;

	@ApiModelProperty(value = "收款类型(1:支付宝  2:微信)")
	private Integer type;

	@ApiModelProperty(value = "审核状态(0:审核中  1:审核通过  2:审核拒绝)")
	private Integer status;

	@ApiModelProperty(value = "审核内容(拒绝填写拒绝理由  成功不用填写)")
	private String content;

	@ApiModelProperty(value = "创建时间")
	private Date time;

	@ApiModelProperty(value = "审核时间")
	private Date examineTime;


}