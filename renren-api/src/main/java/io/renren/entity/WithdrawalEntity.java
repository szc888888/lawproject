package io.renren.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@Data
@TableName("tb_withdrawal")
public class WithdrawalEntity {

    /**
     * 提现表ID（主键ID）
     */
	private Long id;
    /**
     * 用户id
     */
	private Long userId;
    /**
     * 提现用户昵称
     */
	private String userName;
    /**
     * 提现金额
     */
	private BigDecimal withdrawalMoney;
    /**
     * 实到金额(扣除手续费后金额)
     */
	private BigDecimal realityMoney;
    /**
     * 提现前余额
     */
	private BigDecimal frontVacancies;
    /**
     * 提现后余额(当前余额)
     */
	private BigDecimal behindVacancies;
    /**
     * 提现手续费
     */
	private BigDecimal serviceCharge;
    /**
     * 提现手续费比例
     */
	private BigDecimal serviceChargeScale;
    /**
     * 用户收款二维码链接
     */
	private String makeCollectionsUrl;
    /**
     * 收款类型(1:支付宝  2:微信)
     */
	private Integer type;
    /**
     * 审核状态(0:审核中  1:审核通过  2:审核拒绝)
     */
	private Integer status;
    /**
     * 审核内容(拒绝填写拒绝理由  成功不用填写)
     */
	private String content;
    /**
     * 创建时间
     */
	private Date time;
    /**
     * 审核时间
     */
	private Date examineTime;
}