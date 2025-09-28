package io.renren.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@TableName("tb_order")
public class OrderEntity {

    /**
     * 订单表ID(主键ID)
     */
	private Long id;
    /**
     * 充值用户ID
     */
	private Long userId;
    /**
     * 充值用户昵称
     */
	private String userName;
    /**
     * 充值商品ID
     */
	private Long commodityId;
    /**
     * 充值商品名称
     */
	private String commodityName;
    /**
     * 充值商品类型(1:购买条数  2:购买VIP)
     */
	private Integer commodityType;
    /**
     * 充值通道ID
     */
	private Long payId;
    /**
     * 充值通道类型(1:支付宝  2:微信公众号  3:微信H5  4:微信小程序)
     */
	private Integer payType;
    /**
     * 平台订单号
     */
	private String orderCode;
    /**
     * 三方支付订单号(查账订单号)
     */
	private String trilateralCode;
    /**
     * 充值金额(元)
     */
	private BigDecimal rechargeAmount;
    /**
     * 订单支付状态(0:订单创建  1:支付中  2:支付成功 3:支付失败  4:订单超时)
     */
	private Integer orderType;
    /**
     * 订单创建时间
     */
	private Date foundTime;
    /**
     * 订单操作时间(订单状态 创建-支付中-支付成功时间修改)
     */
	private Date operateTime;
    /**
     * 订单超时时间(支付订单一般超过五分钟没有支付为订单超时  订单状态变为4)
     */
	private Date overTime;
    /**
     * 上级返佣金额(邀请返利金额 以逗号分隔 前为一级用户返利  后为二级用户返利)
     */
	private String commission;
}