package io.renren.modules.tmplCategory.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
@ApiModel(value = "提问模板分类")
public class TmplCategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "分类名字")
	private String categoryName;

	@ApiModelProperty(value = "分类的图片")
	private String categoryImg;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "状态(0开启1关闭)")
	private Integer status;


}