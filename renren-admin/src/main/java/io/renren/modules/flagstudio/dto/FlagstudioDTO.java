package io.renren.modules.flagstudio.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Data
@ApiModel(value = "FS绘画配置")
public class FlagstudioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "apikey")
	private String apiKey;

	@ApiModelProperty(value = "根据apikey生成的token,30天生成一次")
	private String token;

	@ApiModelProperty(value = "token过期时间")
	private Date tokenTime;

	@ApiModelProperty(value = "状态(0开启1关闭)")
	private Integer status;

	@ApiModelProperty(value = "添加时间")
	private Date createTime;

	@ApiModelProperty(value = "每日次数(每日一个账号只能画500张图)")
	private Integer dayCount;


}