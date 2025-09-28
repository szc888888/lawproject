package io.renren.modules.tmplQs.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
@ApiModel(value = "提问模板问题")
public class TmplQsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "问题ID")
	private Long id;

	@ApiModelProperty(value = "问题标题")
	private String qsTitle;

	@ApiModelProperty(value = "问题提示")
	private String qsHint;

	@ApiModelProperty(value = "所属分类ID")
	private Long tid;

	@ApiModelProperty(value = "状态(0正常1禁用)")
	private Integer status;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;
	@ApiModelProperty(value = "所属分类名字")
	private String tmplName;
	/**
	 * 问题指引
	 */
	private String qsCall;
}