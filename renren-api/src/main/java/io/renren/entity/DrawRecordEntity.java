package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Data
@TableName("tb_draw_record")
public class DrawRecordEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * 图片地址
     */
	private String imgUrl;
    /**
     * 画图类型(0GPT1SD2MJ)
     */
	private Integer type;
    /**
     * 画图时间
     */
	private Date createTime;
    /**
     * 正向提示词(GPT绘画就只有这个)
     */
	private String prompt;
    /**
     * 反向提示词
     */
	private String negativePrompt;
    /**
     * 用户ID
     */
	private Long userId;
}