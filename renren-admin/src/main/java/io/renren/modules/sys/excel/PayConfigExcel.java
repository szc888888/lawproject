package io.renren.modules.sys.excel;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class PayConfigExcel {
    @Excel(name = "支付通道ID(主键ID)")
    private Long id;
    @Excel(name = "商户APPID")
    private String appId;
    @Excel(name = "商户密钥")
    private String appSecret;
    @Excel(name = "商户私钥")
    private String privateKey;
    @Excel(name = "商户公钥")
    private String publicKey;
    @Excel(name = "支付网关URL")
    private String payUrl;
    @Excel(name = "支付网关URL拼接字段(该字段允许为NULL 应对某些支付需要单独拼接URL字符串)")
    private String payUrlMontage;
    @Excel(name = "支付回调地址")
    private String notifyUrl;
    @Excel(name = "商户名称")
    private String merchantsName;
    @Excel(name = "支付公司名称(例:支付宝,微信,汇付等)")
    private String payName;
    @Excel(name = "支付类型(1:支付宝  2:微信)")
    private Integer type;
    @Excel(name = "是否开启(1:开启  2:关闭)")
    private Integer status;
    @Excel(name = "通道充值成功额度(支付成功)")
    private BigDecimal amountSuccess;
    @Excel(name = "通道备注")
    private String remark;
    @Excel(name = "通道调用名称(该字段请严格按照代码接口填写 否则无法调用支付接口)")
    private String callName;
    @Excel(name = "支付通道图标(用于前端页面显示)")
    private String icon;
    @Excel(name = "配置时间")
    private Date time;

}