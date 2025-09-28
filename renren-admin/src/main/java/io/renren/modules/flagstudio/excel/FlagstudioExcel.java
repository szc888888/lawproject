package io.renren.modules.flagstudio.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@Data
public class FlagstudioExcel {
    @Excel(name = "ID")
    private Long id;
    @Excel(name = "apikey")
    private String apiKey;
    @Excel(name = "根据apikey生成的token,30天生成一次")
    private String token;
    @Excel(name = "最新生成token的时间")
    private Date tokenTime;
    @Excel(name = "状态(0开启1关闭)")
    private Integer status;
    @Excel(name = "添加时间")
    private Date createTime;
    @Excel(name = "每日次数(每日一个账号只能画500张图)")
    private Integer dayCount;

}