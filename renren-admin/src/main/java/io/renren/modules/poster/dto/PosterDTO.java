package io.renren.modules.poster.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Data
@ApiModel(value = "海报设置表")
public class PosterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键ID")
	private Long id;

	@ApiModelProperty(value = "海报主图")
	private String posterImg;

	@ApiModelProperty(value = "海报显示平台logo")
	private String logo;

	@ApiModelProperty(value = "海报分享标题")
	private String title;

	@ApiModelProperty(value = "海报分享内容")
	private String content;


}