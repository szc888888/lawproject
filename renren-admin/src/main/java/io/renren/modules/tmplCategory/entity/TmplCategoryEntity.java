package io.renren.modules.tmplCategory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
@TableName("tb_tmpl_category")
public class TmplCategoryEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 分类名字
     */
	private String categoryName;
    /**
     * 分类的图片
     */
	private String categoryImg;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * 状态(0开启1关闭)
     */
	private Integer status;
}