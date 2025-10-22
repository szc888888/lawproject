package io.renren.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @GetMapping
    public ResponseEntity<IPage<QuestionSampleEntity>> page(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size) {

        Page<QuestionSampleEntity> pager = new Page<>(page, size);
        IPage<QuestionSampleEntity> result = questionSampleService.page(pager);
        return ResponseEntity.ok(result);
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