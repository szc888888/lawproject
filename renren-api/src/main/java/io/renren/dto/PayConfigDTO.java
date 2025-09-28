package io.renren.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@ApiModel(value = "支付通道配置表")
public class PayConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "支付通道ID(主键ID)")
	private Long id;
	
	@ApiModelProperty(value = "微信小程序/微信公众号APPID(JSAPI支付专属)  参数配置对应")
	private String appAppidCode;
	
	@ApiModelProperty(value = "微信小程序/微信公众号密钥(JSAPI支付专属)  参数配置对应")
	private String appSecretCode;

	@ApiModelProperty(value = "商户APPID")
	private String appId;
	
	@ApiModelProperty(value = " 商户号")
	private String merchantId;

	@ApiModelProperty(value = "商户密钥")
	private String appSecret;

	@ApiModelProperty(value = "商户私钥")
	private String privateKey;

	@ApiModelProperty(value = "商户公钥")
	private String publicKey;

	@ApiModelProperty(value = "支付网关URL")
	private String payUrl;

	@ApiModelProperty(value = "支付网关URL拼接字段(该字段允许为NULL 应对某些支付需要单独拼接URL字符串)")
	private String payUrlMontage;

	@ApiModelProperty(value = "支付回调地址")
	private String notifyUrl;
	
	@ApiModelProperty(value = "支付完成后同步跳转地址")
	private String returnUrl;

	@ApiModelProperty(value = "商户名称")
	private String merchantsName;

	@ApiModelProperty(value = "支付公司名称(例:支付宝,微信,汇付等)")
	private String payName;

	@ApiModelProperty(value = "支付类型(1:支付宝  2:微信)")
	private Integer type;

	@ApiModelProperty(value = "是否开启(1:开启  2:关闭)")
	private Integer status;

	@ApiModelProperty(value = "通道充值成功额度(支付成功)")
	private BigDecimal amountSuccess;

	@ApiModelProperty(value = "通道备注")
	private String remark;

	@ApiModelProperty(value = "通道调用名称(该字段请严格按照代码接口填写 否则无法调用支付接口)")
	private String callName;

	@ApiModelProperty(value = "支付通道图标(用于前端页面显示)")
	private String icon;

	@ApiModelProperty(value = "配置时间")
	private Date time;


}