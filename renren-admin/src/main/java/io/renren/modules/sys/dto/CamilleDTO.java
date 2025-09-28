package io.renren.modules.sys.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
@Data
@ApiModel(value = "卡密表")
public class CamilleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "卡密表ID(主键ID)")
	private Long id;

	@ApiModelProperty(value = "卡密名称")
	private String camilleName;

	@ApiModelProperty(value = "卡密号")
	private String camilleNumber;

	@ApiModelProperty(value = "卡密价格")
	private BigDecimal camillePrice;

	@ApiModelProperty(value = "卡密规格(条数/天数)")
	private String camilleSpec;

	@ApiModelProperty(value = "卡密类型(1:购买条数  2:购买VIP)")
	private Integer type;

	@ApiModelProperty(value = "卡密状态  0：可使用   1:已兑换")
	private Integer status;

	@ApiModelProperty(value = "添加时间")
	private Date time;
	
	@ApiModelProperty(value = "生成数量")
	private Integer number;


}