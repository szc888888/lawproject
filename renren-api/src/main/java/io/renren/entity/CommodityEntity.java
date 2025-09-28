package io.renren.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-08
 */
@Data
@TableName("tb_commodity")
public class CommodityEntity {

    /**
     * 商品表ID(主键)
     */
	private Long id;
    /**
     * 商品名称
     */
	private String commodityName;
    /**
     * 商品价格
     */
	private BigDecimal commodityPrice;
    /**
     * 商品规格(条数/天数)
     */
	private String commoditySpec;
    /**
     * 商品类型(1:购买条数  2:购买VIP)
     */
	private Integer type;
    /**
     * 商品备注
     */
	private String remark;
    /**
     * 是否开启(1:是  2:否)
     */
	private Integer status;
    /**
     * 创建时间
     */
	private Date time;
}