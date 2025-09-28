package io.renren.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.dao.QuestionAnswerDao;
import io.renren.dto.ConversationDTO;
import io.renren.entity.QuestionAnswerEntity;
import io.renren.entity.UserEntity;
import io.renren.service.QuestionAnswerService;
import io.renren.vo.TodayQsCount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问答记录表
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@Service
public class QuestionAnswerServiceImpl extends BaseServiceImpl<QuestionAnswerDao, QuestionAnswerEntity> implements QuestionAnswerService {


    @Override
    public List<QuestionAnswerEntity> queryQuestionAnswerList(UserEntity user) {
        return baseDao.queryQuestionAnswerList(user.getId());
    }

    @Override
    public List<QuestionAnswerEntity> queryQuestionAnswerListPage(UserEntity user, String pageNum) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("pageNum",Integer.parseInt(pageNum) * 10);
        return baseDao.queryQuestionAnswerListPage(map);
    }

    @Override
    public TodayQsCount count3And4TodayByUser(Long id) {
        return baseDao.count3And4TodayByUser(id);
    }
    public List<ConversationDTO> getFirstQuestions(Long userId) {
        List<ConversationDTO> list = baseDao.getFirstQuestionPerConversation(userId);
        list.forEach(item -> {
            System.out.println("Conversation: " + item.getConversationId() +
                    ", First Question: " + item.getFirstQuestion());
        });
        return list;
    }

    @Override
    public List<QuestionAnswerEntity> listByConversationId(String conversationId) {
        QueryWrapper<QuestionAnswerEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId)
                .orderByAsc("id"); // 按id排序，也可以按 create_time
        return baseDao.selectList(wrapper);
    }
}