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


@Data
@ApiModel(value = "非流式提问内容/图片等")
public class ChatMsgDTO {
    @ApiModelProperty(value = "msg")
    @NotBlank(message="提问内容不能为空")
    private String msg;

}