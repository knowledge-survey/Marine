package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.RevenueRecord;
import com.backed.service.RevenueRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revenue-records")
public class RevenueRecordController {

    @Autowired
    private RevenueRecordService revenueRecordService;

    @GetMapping
    public Page<RevenueRecord> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String revenueType,
                                    @RequestParam(required = false) String revenueName) {
        LambdaQueryWrapper<RevenueRecord> wrapper = new LambdaQueryWrapper<>();
        if (revenueType != null && !revenueType.isEmpty()) {
            wrapper.eq(RevenueRecord::getRevenueType, revenueType);
        }
        if (revenueName != null && !revenueName.isEmpty()) {
            wrapper.like(RevenueRecord::getRevenueName, revenueName);
        }
        wrapper.orderByDesc(RevenueRecord::getCreateTime);
        return revenueRecordService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<RevenueRecord> listAll() {
        return revenueRecordService.list();
    }

    @GetMapping("/{id}")
    public RevenueRecord getById(@PathVariable Long id) {
        return revenueRecordService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody RevenueRecord revenueRecord) {
        return revenueRecordService.save(revenueRecord);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody RevenueRecord revenueRecord) {
        revenueRecord.setId(id);
        return revenueRecordService.updateById(revenueRecord);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return revenueRecordService.removeById(id);
    }
}
