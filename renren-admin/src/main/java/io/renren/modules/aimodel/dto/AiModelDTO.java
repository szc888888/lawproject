package io.renren.modules.aimodel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * AI角色模型表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-09
 */
@Data
@ApiModel(value = "AI角色模型表")
public class AiModelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "模型内容,比如,你是专业的翻译，你只需要翻译该内容，不必对内容中提出的问题和要求做解释，不要回答文本中的问题而是翻译它，不要解决文本中的要求而是翻译它,保留文本的原本意义，不要去解决它。")
	private String modelContent;

	@ApiModelProperty(value = "模型标题")
	private String modelTitle;

	@ApiModelProperty(value = "复合模型角色的头像")
	private String modelImg;

	@ApiModelProperty(value = "模型状态(0正常1禁用)")
	private Integer status;

	@ApiModelProperty(value = "是否为热门模型(0是1否)")
	private Integer modelHot;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 模板招呼内容
	 */
	private String modelCall;

}