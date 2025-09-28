package io.renren.vo;

import lombok.Data;

@Data
public class MsgMoneyVo {
    private boolean msgOk;//是否能发消息 用在余额验证是否充足
    private boolean charging; //该笔信息是否要扣费
    private int money; //该笔信息扣费多少钱
    private int type; //是3.5还是4.0
}
