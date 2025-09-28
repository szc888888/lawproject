package io.renren.modules.questionAnswer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 问答记录表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Data
@TableName("tb_question_answer")
public class QuestionAnswerEntity {

    /**
     * 
     */
	private Long id;
    /**
     * 用户id
     */
	private Long userId;
    /**
     * 问题
     */
	private String question;
    /**
     * AI的回答
     */
	private String answer;
    /**
     * 产生时间
     */
	private Date createTime;
    /**
     * 问题的tokens
     */
    private Long quesTokens;
    /**
     * 回答的tokens
     */
    private Long ansTokens;
    /**
     * 问题和回答的总tokens
     */
    private Long allTokens;
    /**
     * 类型（0是使用的3.5  1是使用的4.0）
     */
    private Integer type;
}