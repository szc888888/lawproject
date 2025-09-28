/**
 /**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

/**
 * 登录表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "下单支付表单")
public class WithdrawalVo {
    @ApiModelProperty(value = "提现金额")
    @NotBlank(message="请填写提现金额")
    private BigDecimal withdrawalMoney;

    @ApiModelProperty(value = "用户收款二维码链接")
    @NotBlank(message="请添加收款二维码")
    private String makeCollectionsUrl;
    
    @ApiModelProperty(value = "收款类型(1:支付宝  2:微信)")
    @NotBlank(message="请选择收款方式")
    private Integer type;

}