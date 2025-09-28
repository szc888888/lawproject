package io.renren.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Data
@TableName("tb_poster")
public class PosterEntity {

    /**
     * 主键ID
     */
	private Long id;
    /**
     * 海报主图
     */
	private String posterImg;
    /**
     * 海报显示平台logo
     */
	private String logo;
    /**
     * 海报分享标题
     */
	private String title;
    /**
     * 海报分享内容
     */
	private String content;
}