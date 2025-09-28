package io.renren.modules.drawRecord.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Data
@ApiModel(value = "画图记录")
public class DrawRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	private Long id;

	@ApiModelProperty(value = "图片地址")
	private String imgUrl;

	@ApiModelProperty(value = "画图类型(0GPT1SD2MJ)")
	private Integer type;

	@ApiModelProperty(value = "画图时间")
	private Date createTime;

	@ApiModelProperty(value = "正向提示词(GPT绘画就只有这个)")
	private String prompt;

	@ApiModelProperty(value = "反向提示词")
	private String negativePrompt;

	/**
	 * 用户ID
	 */
	private Long userId;
	@ApiModelProperty(value = "用户昵称")
	private String nickName;
}