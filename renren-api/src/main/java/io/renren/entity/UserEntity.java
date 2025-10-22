package io.renren.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.renren.common.utils.DateUtils;
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
	 * 1:普通用户 2 律师用户
	 */
	private Integer type;
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
	 * 账号(可以为空但是不能重复,丢弃不用)
	 */
	private String username;
	/**
	 *密码(丢弃不用)
	 */
	private String password;
	/**
	 *提问次数
	 */
	private Integer msgCount;
	/**
	 * 上级ID
	 */
	private Long pid;
	/**
	 * 爷爷辈ID 最顶级上级ID  三级分销的顶层
	 */
	private Long gpid;
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
	@TableField(exist = false)
	private String vipCount;
	@TableField(exist = false)
	private String imgCount;
	@TableField(exist = false)
	private String remark;

	@TableField(exist = false)
	private String countVip3;
	@TableField(exist = false)
	private String countVip4;
	@TableField(exist = false)
	private String countUser3;
	@TableField(exist = false)
	private String countUser4;
	@TableField(exist = false)
	private String money3;
	@TableField(exist = false)
	private String money4;
	@TableField(exist = false)
	private String fsImgPrice;
	@TableField(exist = false)
	private String sdImgPrice;
	@TableField(exist = false)
	private int todayTreeCount;
	@TableField(exist = false)
	private int todayFourCount;
	/**
	 * 是否普通vip有效期内
	 */
	@JsonIgnore
	public boolean isVip() {
		return vipDate != null && vipDate.after(new Date());
	}

	/**
	 * 是否不允许发提问  就是既不是VIP而且剩余次数为0
	 */
	@JsonIgnore
	public boolean isNotMsg() {
		return !isVip() && this.msgCount == 0;
//		return this.msgCount == 0;
	}
	//增加超级VIP时间
	public Date rechargeVip(int dayCount) {
		if (!isVip()){
			setVipDate(new Date());
		}
		if (dayCount < 0){

		}else if (dayCount == 0){ //测试商品
			setVipDate(DateUtils.getAfterMin(getVipDate(), 1));
		}else {
			setVipDate(DateUtils.getAfterDay(getVipDate(), dayCount));
		}
		return getVipDate();
	}
	public UserEntity(){}
	public UserEntity(String nickName,String wxOpenid,String avatar,int msgCount,Long pid,BigDecimal leveloneProportion,BigDecimal secondaryProportion){
		this.nickName = nickName;
		if(StrUtil.isNotBlank(wxOpenid)){
			this.wxOpenid = wxOpenid;
		}
		this.avatar = avatar;
		this.msgCount = msgCount;
		this.createTime = new Date();
		this.lastLogin = new Date();
		this.vipDate = new Date();
		this.status = 0;
		if(pid != null){
			this.pid = pid;
		}
		this.money = new BigDecimal(0);
		this.leveloneProportion = leveloneProportion;
		this.secondaryProportion = secondaryProportion;
	}
}