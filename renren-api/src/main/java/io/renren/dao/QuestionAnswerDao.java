package io.renren.dao;

import io.renren.common.dao.BaseDao;
import io.renren.dto.ConversationDTO;
import io.renren.entity.QuestionAnswerEntity;
import io.renren.vo.TodayQsCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 问答记录表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Mapper
public interface QuestionAnswerDao extends BaseDao<QuestionAnswerEntity> {

    List<QuestionAnswerEntity> queryQuestionAnswerList(Long id);

    List<QuestionAnswerEntity> queryQuestionAnswerListPage(Map<String, Object> map);

    TodayQsCount count3And4TodayByUser(Long id);
    List<ConversationDTO> getFirstQuestionPerConversation(@Param("userId")long userId);
}