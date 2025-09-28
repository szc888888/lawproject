package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
@TableName("tb_species_list")
public class SpeciesListEntity {

    /**
     * 主键
     */
	private Long id;
    /**
     * 用户id
     */
	private Long userid;
    /**
     * 聊天币数量
     */
	private Integer species;
    /**
     * 产生时间
     */
	private Date createTime;
    /**
     * 提问内容
     */
	private String msgContent;
    /**
     * 类型(0问答1图片2平台赠送3充值4邀请奖励5签到赠送)
     */
	private Integer type;
}