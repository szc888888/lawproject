package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Data
@TableName("tb_flagstudio")
public class FlagstudioEntity {

    /**
     * ID
     */
	private Long id;
    /**
     * apikey
     */
	private String apiKey;
    /**
     * 根据apikey生成的token,30天生成一次
     */
	private String token;
    /**
     * 最新生成token的时间
     */
	private Date tokenTime;
    /**
     * 状态(0开启1关闭)
     */
	private Integer status;
    /**
     * 添加时间
     */
	private Date createTime;
    /**
     * 每日次数(每日一个账号只能画500张图)
     */
	private Integer dayCount;
}