package io.renren.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.entity.QuestionSampleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis-Plus Mapper (DAO) for tb_question_sample.
 *
 * 使用 MyBatis-Plus 时通常只需要继承 BaseMapper 即可获得常用的 CRUD 方法。
 * 如果需要自定义 SQL，可以在同名的 XML 文件中添加 SQL（示例在下一个文件）。
 */
@Mapper
@Repository
public interface QuestionSampleMapper extends BaseMapper<QuestionSampleEntity> {

    /**
     * 自定义方法示例：根据 parentId 查询子项
     * 如果不需要自定义 SQL，可删掉此方法。
     */
//    List<QuestionSampleEntity> selectByParentId(@Param("parentId") String parentId);
}