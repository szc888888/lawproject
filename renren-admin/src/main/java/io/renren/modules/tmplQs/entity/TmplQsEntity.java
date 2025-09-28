package io.renren.modules.tmplQs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
@TableName("tb_tmpl_qs")
public class TmplQsEntity {

    /**
     * 问题ID
     */
	private Long id;
    /**
     * 问题标题
     */
	private String qsTitle;
    /**
     * 问题提示
     */
	private String qsHint;
    /**
     * 所属分类ID
     */
	private Long tid;
    /**
     * 状态(0正常1禁用)
     */
	private Integer status;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * 问题指引
     */
    private String qsCall;
}