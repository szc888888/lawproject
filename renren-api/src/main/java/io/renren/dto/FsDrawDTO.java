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
@ApiModel(value = "Fs绘画参数")
public class FsDrawDTO {
    @ApiModelProperty(value = "正向提示词")
    @NotBlank(message="提示词不能为空")
    private String prompt;
    @ApiModelProperty(value = "反向提示词")
    private String negative_prompts;
    @ApiModelProperty(value = "制导比例")
    private String guidance_scale;
    /**
     * 生成图片的宽度，可选，默认512。宽*高不能超过768*768=589824
     */
    @ApiModelProperty(value = "图片宽度")
    private String width;
    /**
     * 生成图片的宽度，可选，默认512。宽*高不能超过768*768=589824
     */
    @ApiModelProperty(value = "图片高度")
    private String height;
    /**
     * 去噪的步骤数。更多的steps会获得更高质量的图像。
     * 默认值: 50
     * 取值范围：[10, 100]
     */
    @ApiModelProperty(value = "采样步数")
    private String steps;
    @ApiModelProperty(value = "采样器")
    private String sampler;
    /**
     * 生成图片的风格配置。支持的风格有：
     * [国画, 写实主义, 虚幻引擎, 黑白插画, 版绘, 低聚, 工业霓虹, 电影艺术, 史诗大片, 暗黑, 涂鸦, 漫画场景, 特写, 儿童画, 油画, 水彩画, 素描, 卡通画, 浮世绘, 赛博朋克, 吉卜力, 哑光, 现代中式, 相机, CG渲染, 动漫, 霓虹游戏, 蒸汽波, 宝可梦, 火影忍者, 圣诞老人, 个人特效, 通用漫画, Momoko, MJ风格, 剪纸, 齐白石, 张大千, 丰子恺, 毕加索, 梵高, 塞尚, 莫奈, 马克·夏加尔, 丢勒, 米开朗基罗, 高更, 爱德华·蒙克, 托马斯·科尔, 安迪·霍尔, 新海诚, 倪传婧, 村上隆, 黄光剑, 吴冠中, 林风眠, 木内达朗, 萨雷尔, 杜拉克, 比利宾, 布拉德利, 普罗旺森, 莫比乌斯, 格里斯利, 比普, 卡尔·西松, 玛丽·布莱尔, 埃里克·卡尔, 扎哈·哈迪德, 包豪斯, 英格尔斯, RHADS, 阿泰·盖兰, 俊西, 坎皮恩, 德尚鲍尔, 库沙特, 雷诺阿]
     */
    @ApiModelProperty(value = "风格")
    private String style;
    @ApiModelProperty(value = "随机种子")
    private int seed;

    @ApiModelProperty(value = "张数")
    private int fsCount;
}