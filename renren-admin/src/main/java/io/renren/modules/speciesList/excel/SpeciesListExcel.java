package io.renren.modules.speciesList.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 问答次数流水
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-09
 */
@Data
public class SpeciesListExcel {
    @Excel(name = "主键")
    private Long id;
    @Excel(name = "用户id")
    private Long userid;
    @Excel(name = "聊天币数量")
    private Integer species;
    @Excel(name = "产生时间")
    private Date createTime;
    @Excel(name = "提问内容")
    private String msgContent;
    @Excel(name = "类型(0问答1图片2平台赠送3充值4邀请奖励)")
    private Integer type;

}