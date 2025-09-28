/**
 /**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "下单支付表单")
public class PayDTO {
    @ApiModelProperty(value = "支付通道ID")
    @NotBlank(message="请选择一个支付通道")
    private Long payId;

    @ApiModelProperty(value = "商品Id")
    @NotBlank(message="请选择一个商品")
    private Long commodityId;
    
    @ApiModelProperty(value = "用户微信唯一标识")
    private String openId;
    
    @ApiModelProperty(value = "用户Id")
    private Long userId;

}