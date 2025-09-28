package io.renren.modules.pdfDoc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Data
@ApiModel(value = "PDF和DOC表")
public class PdfDocDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键ID")
	private Long id;

	@ApiModelProperty(value = "文件名")
	private String fileName;

	@ApiModelProperty(value = "类型(0是PDF 1doc)")
	private Integer type;

	@ApiModelProperty(value = "用户id")
	private Long userId;

	@ApiModelProperty(value = "上传时间")
	private Date createTime;


}