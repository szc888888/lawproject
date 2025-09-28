package io.renren.modules.pdfDoc.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * PDF和DOC表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-06-06
 */
@Data
public class PdfDocExcel {
    @Excel(name = "主键ID")
    private Long id;
    @Excel(name = "文件名")
    private String fileName;
    @Excel(name = "类型(0是PDF 1doc)")
    private Integer type;
    @Excel(name = "用户id")
    private Long userId;
    @Excel(name = "上传时间")
    private Date createTime;

}