package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.SalesRecord;
import com.backed.service.SalesRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-records")
public class SalesRecordController {

    @Autowired
    private SalesRecordService salesRecordService;

    @GetMapping
    public Page<SalesRecord> list(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String productName,
                                  @RequestParam(required = false) String customerName) {
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(SalesRecord::getProductName, productName);
        }
        if (customerName != null && !customerName.isEmpty()) {
            wrapper.like(SalesRecord::getCustomerName, customerName);
        }
        wrapper.orderByDesc(SalesRecord::getCreateTime);
        return salesRecordService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<SalesRecord> listAll() {
        return salesRecordService.list();
    }

    @GetMapping("/{id}")
    public SalesRecord getById(@PathVariable Long id) {
        return salesRecordService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody SalesRecord salesRecord) {
        return salesRecordService.save(salesRecord);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody SalesRecord salesRecord) {
        salesRecord.setId(id);
        return salesRecordService.updateById(salesRecord);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return salesRecordService.removeById(id);
    }
}
