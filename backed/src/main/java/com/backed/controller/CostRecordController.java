package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.CostRecord;
import com.backed.service.CostRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cost-records")
public class CostRecordController {

    @Autowired
    private CostRecordService costRecordService;

    @GetMapping
    public Page<CostRecord> list(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String costType,
                                 @RequestParam(required = false) String costName) {
        LambdaQueryWrapper<CostRecord> wrapper = new LambdaQueryWrapper<>();
        if (costType != null && !costType.isEmpty()) {
            wrapper.eq(CostRecord::getCostType, costType);
        }
        if (costName != null && !costName.isEmpty()) {
            wrapper.like(CostRecord::getCostName, costName);
        }
        wrapper.orderByDesc(CostRecord::getCreateTime);
        return costRecordService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<CostRecord> listAll() {
        return costRecordService.list();
    }

    @GetMapping("/{id}")
    public CostRecord getById(@PathVariable Long id) {
        return costRecordService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody CostRecord costRecord) {
        return costRecordService.save(costRecord);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody CostRecord costRecord) {
        costRecord.setId(id);
        return costRecordService.updateById(costRecord);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return costRecordService.removeById(id);
    }
}
