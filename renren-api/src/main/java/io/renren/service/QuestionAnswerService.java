package io.renren.service;

import io.renren.common.service.BaseService;
import io.renren.dto.ConversationDTO;
import io.renren.entity.QuestionAnswerEntity;
import io.renren.entity.UserEntity;
import io.renren.vo.TodayQsCount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 问答记录表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
public interface QuestionAnswerService extends BaseService<QuestionAnswerEntity> {

    List<QuestionAnswerEntity> queryQuestionAnswerList(UserEntity user);

    List<QuestionAnswerEntity> queryQuestionAnswerListPage(UserEntity user, String pageNum);

    TodayQsCount count3And4TodayByUser(Long id);

    public List<ConversationDTO> getFirstQuestions(Long userId);
    List<QuestionAnswerEntity> listByConversationId(String conversationId);
}