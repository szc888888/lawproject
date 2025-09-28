package io.renren.modules.poster.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 海报设置表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-25
 */
@Data
public class PosterExcel {
    @Excel(name = "主键ID")
    private Long id;
    @Excel(name = "海报主图")
    private String posterImg;
    @Excel(name = "海报显示平台logo")
    private String logo;
    @Excel(name = "海报分享标题")
    private String title;
    @Excel(name = "海报分享内容")
    private String content;

}