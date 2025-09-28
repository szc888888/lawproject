package io.renren.modules.drawRecord.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@Data
public class DrawRecordExcel {
    @Excel(name = "ID")
    private Long id;
    @Excel(name = "图片地址")
    private String imgUrl;
    @Excel(name = "画图类型(0GPT1SD2MJ)")
    private Integer type;
    @Excel(name = "画图时间")
    private Date createTime;
    @Excel(name = "正向提示词(GPT绘画就只有这个)")
    private String prompt;
    @Excel(name = "反向提示词")
    private String negativePrompt;

}