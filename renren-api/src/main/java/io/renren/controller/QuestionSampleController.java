package io.renren.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.Result;
import io.renren.dto.QuestionSampleDTO;
import io.renren.entity.QuestionSampleEntity;
import io.renren.service.QuestionSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for QuestionSample CRUD
 *
 * - 分页: GET /api/question-samples?page=1&size=10
 * - 获取: GET /api/question-samples/{id}
 * - 新增: POST /api/question-samples
 * - 更新: PUT /api/question-samples
 * - 删除: DELETE /api/question-samples/{id}
 */
@RestController
@RequestMapping("/api/question-samples")
public class QuestionSampleController {

    private final QuestionSampleService questionSampleService;

    @Autowired
    public QuestionSampleController(QuestionSampleService questionSampleService) {
        this.questionSampleService = questionSampleService;
    }

    /**
     * 分页查询
     */

//    public Result page(
//            @RequestParam(value = "page", defaultValue = "1") long page,
//            @RequestParam(value = "size", defaultValue = "10") long size) {
//    @GetMapping
    @PostMapping("queryHighFrequencyQuestions")
    public Result page(@RequestBody
                       QuestionSampleDTO questionSampleDTO) {
        Page<QuestionSampleEntity> pager = new Page<>(questionSampleDTO.getPage(), questionSampleDTO.getSize());

        // 创建查询条件
        QueryWrapper<QuestionSampleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("parent_id").orderByAsc("code"); ; // 只查询 parentId 为 null 的记录

        IPage<QuestionSampleEntity> result = questionSampleService.page(pager, queryWrapper);
//        return ResponseEntity.ok(result);
        return new Result().ok(result);
    }

    /**
     * 根据 id 查询
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionSampleEntity> getById(@PathVariable("id") String id) {
        QuestionSampleEntity entity = questionSampleService.getById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }

    /**
     * 新增（id 会由 MyBatis-Plus 的 IdType.ASSIGN_UUID 自动生成）
     */
    @PostMapping
    public ResponseEntity<QuestionSampleEntity> create(@RequestBody QuestionSampleEntity entity) {
        boolean saved = questionSampleService.save(entity);
        if (saved) {
            return ResponseEntity.ok(entity);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新（根据 entity.id）
     */
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody QuestionSampleEntity entity) {
        boolean updated = questionSampleService.updateById(entity);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        boolean removed = questionSampleService.removeById(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}