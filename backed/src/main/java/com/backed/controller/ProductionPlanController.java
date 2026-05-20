package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.AquacultureZone;
import com.backed.entity.ProductionPlan;
import com.backed.service.AquacultureZoneService;
import com.backed.service.ProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production-plans")
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;

    @Autowired
    private AquacultureZoneService aquacultureZoneService;

    @GetMapping
    public Page<ProductionPlan> list(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String planName,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) Long zoneId) {
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        if (planName != null && !planName.isEmpty()) {
            wrapper.like(ProductionPlan::getPlanName, planName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ProductionPlan::getStatus, status);
        }
        if (zoneId != null) {
            wrapper.eq(ProductionPlan::getZoneId, zoneId);
        }
        wrapper.orderByDesc(ProductionPlan::getCreateTime);
        Page<ProductionPlan> result = productionPlanService.page(new Page<>(page, size), wrapper);
        // 填充zoneName
        result.getRecords().forEach(plan -> {
            if (plan.getZoneName() == null || plan.getZoneName().isEmpty()) {
                aquacultureZoneService.findById(plan.getZoneId()).ifPresent(zone -> {
                    plan.setZoneName(zone.getName());
                });
            }
        });
        return result;
    }

    @GetMapping("/all")
    public List<ProductionPlan> listAll() {
        List<ProductionPlan> list = productionPlanService.list();
        // 填充zoneName
        list.forEach(plan -> {
            if (plan.getZoneName() == null || plan.getZoneName().isEmpty()) {
                aquacultureZoneService.findById(plan.getZoneId()).ifPresent(zone -> {
                    plan.setZoneName(zone.getName());
                });
            }
        });
        return list;
    }

    @GetMapping("/{id}")
    public ProductionPlan getById(@PathVariable Long id) {
        ProductionPlan plan = productionPlanService.getById(id);
        if (plan != null && (plan.getZoneName() == null || plan.getZoneName().isEmpty())) {
            aquacultureZoneService.findById(plan.getZoneId()).ifPresent(zone -> {
                plan.setZoneName(zone.getName());
            });
        }
        return plan;
    }

    @PostMapping
    public boolean save(@RequestBody ProductionPlan productionPlan) {
        // 自动设置zoneName
        if (productionPlan.getZoneId() != null) {
            aquacultureZoneService.findById(productionPlan.getZoneId()).ifPresent(zone -> {
                productionPlan.setZoneName(zone.getName());
            });
        }
        return productionPlanService.save(productionPlan);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody ProductionPlan productionPlan) {
        productionPlan.setId(id);
        // 自动设置zoneName
        if (productionPlan.getZoneId() != null) {
            aquacultureZoneService.findById(productionPlan.getZoneId()).ifPresent(zone -> {
                productionPlan.setZoneName(zone.getName());
            });
        }
        return productionPlanService.updateById(productionPlan);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return productionPlanService.removeById(id);
    }
}
