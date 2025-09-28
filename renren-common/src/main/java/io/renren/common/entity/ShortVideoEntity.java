package io.renren.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 短视频拉取数据
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-07-11
 */
@Data
@TableName("tb_short_video")
public class ShortVideoEntity {

    /**
     * id
     */
	private Long id;
    /**
     * 视频地址
     */
	private String videoUrl;
    /**
     * 封面图片
     */
	private String imgUrl;
    /**
     * BMG地址
     */
	private String voiceUrl;
    /**
     * 视频标题
     */
	private String title;
    /**
     * 视频内容文案
     */
	private String content;
    /**
     * 用户ID
     */
	private Long userId;
    /**
     * 拉取时间
     */
	private Date createTime;
    public ShortVideoEntity(){}
    public ShortVideoEntity(Long userId,String videoUrl,String voiceUrl,String imgUrl,String title,String content){
        this.userId = userId;
        this.videoUrl = videoUrl;
        this.voiceUrl = voiceUrl;
        this.imgUrl = imgUrl;
        this.title = title;
        this.content = content;
        this.createTime = new Date();
    }
}