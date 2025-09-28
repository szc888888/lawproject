package io.renren.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 订单表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@ApiModel(value = "订单表")
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "订单表ID(主键ID)")
	private Long id;

	@ApiModelProperty(value = "充值用户ID")
	private Long userId;

	@ApiModelProperty(value = "充值用户昵称")
	private String userName;

	@ApiModelProperty(value = "充值商品ID")
	private Long commodityId;

	@ApiModelProperty(value = "充值商品名称")
	private String commodityName;

	@ApiModelProperty(value = "充值商品类型(1:购买条数  2:购买VIP)")
	private Integer commodityType;

	@ApiModelProperty(value = "充值通道ID")
	private Long payId;

	@ApiModelProperty(value = "充值通道类型(1:支付宝  2:微信)")
	private Integer payType;

	@ApiModelProperty(value = "平台订单号")
	private String orderCode;

	@ApiModelProperty(value = "三方支付订单号(查账订单号)")
	private String trilateralCode;

	@ApiModelProperty(value = "充值金额(元)")
	private BigDecimal rechargeAmount;

	@ApiModelProperty(value = "订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)")
	private Integer orderType;

	@ApiModelProperty(value = "订单创建时间")
	private Date foundTime;

	@ApiModelProperty(value = "订单操作时间(订单状态 创建-支付中-支付成功时间修改)")
	private Date operateTime;

	@ApiModelProperty(value = "订单超时时间(支付订单一般超过五分钟没有支付为订单超时  订单状态变为4)")
	private Date overTime;

	@ApiModelProperty(value = "上级返佣金额(邀请返利金额 以逗号分隔 前为一级用户返利  后为二级用户返利)")
	private String commission;
	
	
	@ApiModelProperty(value = "卡密规格")
	private String camilleSpec;


}