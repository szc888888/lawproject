package io.renren.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.dao.QuestionSampleMapper;
import org.springframework.stereotype.Service;
import io.renren.service.QuestionSampleService;
import io.renren.entity.QuestionSampleEntity;
/**
 * ServiceImpl - QuestionSampleEntity
 */
@Service
public class QuestionSampleServiceImpl extends ServiceImpl<QuestionSampleMapper, QuestionSampleEntity>
        implements QuestionSampleService {
    // 如需自定义方法实现，添加在此处
}