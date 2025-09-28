package io.renren.modules.tmplQs.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
public class TmplQsExcel {
    @Excel(name = "问题ID")
    private Long id;
    @Excel(name = "问题标题")
    private String qsTitle;
    @Excel(name = "问题提示")
    private String qsHint;
    @Excel(name = "所属分类ID")
    private Long tid;
    @Excel(name = "状态(0正常1禁用)")
    private Integer status;
    @Excel(name = "创建时间")
    private Date createTime;

}