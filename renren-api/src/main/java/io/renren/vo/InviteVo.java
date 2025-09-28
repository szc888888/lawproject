package io.renren.vo;

import lombok.Data;

@Data
public class InviteVo {
    private String h5Url;
    private String inviteTitle;
    private String inviteImgUrl;
    private String inviteSummary;
    private String invitePosterUrl;
    private Long pid;
}
