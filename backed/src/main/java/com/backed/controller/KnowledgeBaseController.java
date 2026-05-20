package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.KnowledgeBase;
import com.backed.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @GetMapping
    public Page<KnowledgeBase> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String title) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(KnowledgeBase::getCategory, category);
        }
        if (title != null && !title.isEmpty()) {
            wrapper.like(KnowledgeBase::getTitle, title);
        }
        wrapper.orderByDesc(KnowledgeBase::getCreateTime);
        return knowledgeBaseService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<KnowledgeBase> listAll() {
        return knowledgeBaseService.list();
    }

    @GetMapping("/{id}")
    public KnowledgeBase getById(@PathVariable Long id) {
        return knowledgeBaseService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody KnowledgeBase knowledgeBase) {
        return knowledgeBaseService.save(knowledgeBase);
    }

    @PutMapping
    public boolean updateById(@RequestBody KnowledgeBase knowledgeBase) {
        return knowledgeBaseService.updateById(knowledgeBase);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return knowledgeBaseService.removeById(id);
    }
}
