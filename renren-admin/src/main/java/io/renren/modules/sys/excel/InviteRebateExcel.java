package io.renren.modules.sys.excel;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请返利表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-10
 */
@Data
public class InviteRebateExcel {
    @Excel(name = "邀请返利ID(主键ID)")
    private Long id;
    @Excel(name = "获得返利用户ID")
    private Long userId;
    @Excel(name = "获得返利用户名称")
    private String userName;
    @Excel(name = "返利比例")
    private BigDecimal scale;
    @Excel(name = "订单充值金额")
    private BigDecimal money;
    @Excel(name = "获得金额")
    private BigDecimal acquireMoney;
    @Excel(name = "下级级别(下一级,下二级)")
    private String belowRank;
    @Excel(name = "下级用户ID")
    private Long belowUserId;
    @Excel(name = "下级用户名称")
    private String belowUserName;
    @Excel(name = "关联订单ID")
    private Long orderId;
    @Excel(name = "创建时间")
    private Date time;

}