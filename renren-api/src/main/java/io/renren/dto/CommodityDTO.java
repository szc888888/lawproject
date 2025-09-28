package io.renren.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 商品表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Data
@ApiModel(value = "商品表")
public class CommodityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "商品表ID(主键)")
	private Long id;

	@ApiModelProperty(value = "商品名称")
	private String commodityName;

	@ApiModelProperty(value = "商品价格")
	private BigDecimal commodityPrice;

	@ApiModelProperty(value = "商品规格(条数/天数)")
	private String commoditySpec;

	@ApiModelProperty(value = "商品类型(1:购买条数  2:购买VIP)")
	private Integer type;

	@ApiModelProperty(value = "商品备注")
	private String remark;

	@ApiModelProperty(value = "是否开启(1:是  2:否)")
	private Integer status;

	@ApiModelProperty(value = "创建时间")
	private Date time;


}