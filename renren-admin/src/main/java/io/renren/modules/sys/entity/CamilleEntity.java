package io.renren.modules.sys.entity;


import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_camille")
public class CamilleEntity {

    /**
     * 卡密表ID(主键ID)
     */
	private Long id;
    /**
     * 卡密名称
     */
	private String camilleName;
    /**
     * 卡密号
     */
	private String camilleNumber;
    /**
     * 卡密价格
     */
	private BigDecimal camillePrice;
    /**
     * 卡密规格(条数/天数)
     */
	private String camilleSpec;
    /**
     * 卡密类型(1:购买条数  2:购买VIP)
     */
	private Integer type;
    /**
     * 卡密状态  0：可使用   1:已兑换
     */
	private Integer status;
    /**
     * 添加时间
     */
	private Date time;
}