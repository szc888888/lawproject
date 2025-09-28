package io.renren.dto;


import lombok.Data;

import java.util.Date;

@Data
public class ConversationDTO {
    private String conversationId;
    private String firstQuestion;
    private Date createTime;
}