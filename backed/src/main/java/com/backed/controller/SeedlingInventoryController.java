package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.SeedlingInventory;
import com.backed.service.SeedlingInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seedling-inventory")
public class SeedlingInventoryController {

    @Autowired
    private SeedlingInventoryService seedlingInventoryService;

    @GetMapping
    public Page<SeedlingInventory> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String species,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String zoneName) {
        LambdaQueryWrapper<SeedlingInventory> wrapper = new LambdaQueryWrapper<>();
        if (species != null && !species.isEmpty()) {
            wrapper.like(SeedlingInventory::getSeedlingName, species);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(SeedlingInventory::getStatus, status);
        }
        if (zoneName != null && !zoneName.isEmpty()) {
            wrapper.eq(SeedlingInventory::getZoneName, zoneName);
        }
        wrapper.orderByDesc(SeedlingInventory::getCreateTime);
        return seedlingInventoryService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<SeedlingInventory> listAll() {
        return seedlingInventoryService.list();
    }

    @GetMapping("/{id}")
    public SeedlingInventory getById(@PathVariable Long id) {
        return seedlingInventoryService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody SeedlingInventory seedlingInventory) {
        return seedlingInventoryService.save(seedlingInventory);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody SeedlingInventory seedlingInventory) {
        seedlingInventory.setId(id);
        return seedlingInventoryService.updateById(seedlingInventory);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return seedlingInventoryService.removeById(id);
    }
}
