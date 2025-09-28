package io.renren.modules.sys.excel;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
@Data
public class CamilleExcel {
    @Excel(name = "卡密表ID(主键ID)")
    private Long id;
    @Excel(name = "卡密名称")
    private String camilleName;
    @Excel(name = "卡密号")
    private String camilleNumber;
    @Excel(name = "卡密价格")
    private BigDecimal camillePrice;
    @Excel(name = "卡密规格(条数/天数)")
    private String camilleSpec;
    @Excel(name = "卡密类型(1:购买条数  2:购买VIP)")
    private Integer type;
    @Excel(name = "卡密状态  0：可使用   1:已兑换")
    private Integer status;
    @Excel(name = "添加时间")
    private Date time;

}