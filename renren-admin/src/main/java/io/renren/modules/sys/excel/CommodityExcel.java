package io.renren.modules.sys.excel;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class CommodityExcel {
    @Excel(name = "商品表ID(主键)")
    private Long id;
    @Excel(name = "商品名称")
    private String commodityName;
    @Excel(name = "商品价格")
    private BigDecimal commodityPrice;
    @Excel(name = "商品规格(条数/天数)")
    private String commoditySpec;
    @Excel(name = "商品类型(1:购买条数  2:购买VIP)")
    private Integer type;
    @Excel(name = "商品备注")
    private String remark;
    @Excel(name = "是否开启(1:是  2:否)")
    private Integer status;
    @Excel(name = "创建时间")
    private Date time;

}