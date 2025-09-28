package io.renren.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@ApiModel(value = "问答次数流水")
public class SpeciesListDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键")
	private Long id;

	@ApiModelProperty(value = "用户id")
	private Long userid;

	@ApiModelProperty(value = "聊天币数量")
	private Integer species;

	@ApiModelProperty(value = "产生时间")
	private Date createTime;

	@ApiModelProperty(value = "提问内容")
	private String msgContent;

	@ApiModelProperty(value = "类型(0问答1图片2平台赠送3充值4邀请奖励)")
	private Integer type;


}