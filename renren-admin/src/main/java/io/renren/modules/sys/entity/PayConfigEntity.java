package io.renren.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付通道配置表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@TableName("tb_pay_config")
public class PayConfigEntity {

    /**
     * 支付通道ID(主键ID)
     */
	private Long id;
    /**
     * 微信小程序/微信公众号APPID(JSAPI支付专属)  参数配置对应
     */
	private String appAppidCode;
    /**
     * 微信小程序/微信公众号密钥(JSAPI支付专属)  参数配置对应
     */
	private String appSecretCode;
    /**
     * 商户APPID
     */
	private String appId;
    /**
     * 商户号
     */
	private String merchantId;
    /**
     * 商户密钥
     */
	private String appSecret;
    /**
     * 商户私钥
     */
	private String privateKey;
    /**
     * 商户公钥
     */
	private String publicKey;
    /**
     * 支付网关URL
     */
	private String payUrl;
    /**
     * 支付网关URL拼接字段(该字段允许为NULL 应对某些支付需要单独拼接URL字符串)
     */
	private String payUrlMontage;
    /**
     * 支付回调地址
     */
	private String notifyUrl;
    /**
     * 支付完成后同步跳转地址
     */
	private String returnUrl;
    /**
     * 商户名称
     */
	private String merchantsName;
    /**
     * 支付公司名称(例:支付宝,微信,汇付等)
     */
	private String payName;
    /**
     * 支付类型(1:支付宝  2:微信公众号  3:微信H5  4:微信小程序 6:微信PC扫码  7:支付宝PC扫码)
     */
	private Integer type;
    /**
     * 是否开启(1:开启  2:关闭)
     */
	private Integer status;
    /**
     * 通道充值成功额度(支付成功)
     */
	private BigDecimal amountSuccess;
    /**
     * 通道备注
     */
	private String remark;
    /**
     * 通道调用名称(该字段请严格按照代码接口填写 否则无法调用支付接口)
     */
	private String callName;
    /**
     * 支付通道图标(用于前端页面显示)
     */
	private String icon;
    /**
     * 配置时间
     */
	private Date time;
}