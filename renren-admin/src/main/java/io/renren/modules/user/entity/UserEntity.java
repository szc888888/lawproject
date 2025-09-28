package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-07
 */
@Data
@TableName("tb_user")
public class UserEntity {

    /**
     * 用户id(主键)
     */
	private Long id;
    /**
     * 手机号
     */
	private String mobile;
    /**
     * 普通用户的标识，对当前开发者帐号唯一
     */
	private String wxOpenid;
    /**
     * 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的
     */
	private String wxUnionid;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * VIP到期时间
     */
	private Date vipDate;
    /**
     * 最后登录时间
     */
	private Date lastLogin;
    /**
     * 0:正常状态 1:封禁状态
     */
	private Integer status;
    /**
     * 封禁原因
     */
	private String lockReason;
    /**
     * 昵称
     */
	private String nickName;
    /**
     * 头像地址
     */
	private String avatar;
    /**
     * ip
     */
	private String ip;
    /**
     * 渠道号
     */
	private String channel;
    /**
     * 账号(可以为空但是不能重复)
     */
	private String username;
    /**
     * 
     */
	private String password;
    /**
     *提问次数
     */
    private Integer msgCount;
	/**
	 * 用户可提现余额
	 */
	private BigDecimal money;
	/**
	 * 用户下一级返利比例
	 */
	private BigDecimal leveloneProportion;
	/**
	 * 用户下二级返利比例
	 */
	private BigDecimal secondaryProportion;
	/**
	 * 爷爷辈ID 最顶级上级ID  三级分销的顶层
	 */
	private Long gpid;
	/**
	 * 是否普通vip有效期内
	 */
	@JsonIgnore
	public boolean isVip() {
		return vipDate != null && vipDate.after(new Date());
	}
}