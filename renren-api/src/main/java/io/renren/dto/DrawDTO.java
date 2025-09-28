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
@ApiModel(value = "绘画参数")
public class DrawDTO {
    @ApiModelProperty(value = "正向提示词")
    @NotBlank(message="提示词不能为空")
    private String prompt;
    @ApiModelProperty(value = "反向提示词")
    private String negativePrompt;
    @ApiModelProperty(value = "图片宽度")
    private String width;
    @ApiModelProperty(value = "图片高度")
    private String height;
    @ApiModelProperty(value = "采样步数")
    private String steps;
    @ApiModelProperty(value = "采样方法")
    private String samplerIndex;
    @ApiModelProperty(value = "提示词相关性")
    private int cfgScale;
    @ApiModelProperty(value = "随机种子")
    private int seed;
    @ApiModelProperty(value = "张数")
    private int batchSize;
}