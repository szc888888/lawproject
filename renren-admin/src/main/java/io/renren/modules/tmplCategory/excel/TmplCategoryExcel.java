package io.renren.modules.tmplCategory.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
public class TmplCategoryExcel {
    @Excel(name = "")
    private Long id;
    @Excel(name = "分类名字")
    private String categoryName;
    @Excel(name = "分类的图片")
    private String categoryImg;
    @Excel(name = "创建时间")
    private Date createTime;
    @Excel(name = "状态(0开启1关闭)")
    private Integer status;

}