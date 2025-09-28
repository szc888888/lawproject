package io.renren.entity;


import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_invite_rebate")
public class InviteRebateEntity {

    /**
     * 邀请返利ID(主键ID)
     */
	private Long id;
    /**
     * 获得返利用户ID
     */
	private Long userId;
    /**
     * 获得返利用户名称
     */
	private String userName;
    /**
     * 返利比例
     */
	private BigDecimal scale;
    /**
     * 订单充值金额
     */
	private BigDecimal money;
    /**
     * 返利前余额
     */
	private BigDecimal frontVacancies;
    /**
     * 获取佣金后余额(当前余额)
     */
	private BigDecimal behindVacancies;
    /**
     * 获得金额
     */
	private BigDecimal acquireMoney;
    /**
     * 下级级别(下一级,下二级)
     */
	private String belowRank;
    /**
     * 下级用户ID
     */
	private Long belowUserId;
    /**
     * 下级用户名称
     */
	private String belowUserName;
    /**
     * 关联订单ID
     */
	private Long orderId;
    /**
     * 创建时间
     */
	private Date time;
}