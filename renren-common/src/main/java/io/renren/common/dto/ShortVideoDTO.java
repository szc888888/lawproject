package io.renren.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 短视频拉取数据
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-07-11
 */
@Data
@ApiModel(value = "短视频拉取数据")
public class ShortVideoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "视频地址")
	private String videoUrl;

	@ApiModelProperty(value = "封面图片")
	private String imgUrl;

	@ApiModelProperty(value = "BMG地址")
	private String voiceUrl;

	@ApiModelProperty(value = "视频标题")
	private String title;

	@ApiModelProperty(value = "视频内容文案")
	private String content;

	@ApiModelProperty(value = "用户ID")
	private Long userId;

	@ApiModelProperty(value = "拉取时间")
	private Date createTime;


}