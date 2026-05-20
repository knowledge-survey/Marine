package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.FeedInventory;
import com.backed.service.FeedInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed-inventory")
public class FeedInventoryController {

    @Autowired
    private FeedInventoryService feedInventoryService;

    @GetMapping
    public Page<FeedInventory> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String feedName,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String zoneName) {
        LambdaQueryWrapper<FeedInventory> wrapper = new LambdaQueryWrapper<>();
        if (zoneName != null && !zoneName.isEmpty()) {
            wrapper.eq(FeedInventory::getZoneName, zoneName);
        }
        if (feedName != null && !feedName.isEmpty()) {
            wrapper.like(FeedInventory::getFeedName, feedName);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(FeedInventory::getStatus, status);
        }
        wrapper.orderByDesc(FeedInventory::getCreateTime);
        return feedInventoryService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<FeedInventory> listAll() {
        return feedInventoryService.list();
    }

    @GetMapping("/{id}")
    public FeedInventory getById(@PathVariable Long id) {
        return feedInventoryService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody FeedInventory feedInventory) {
        return feedInventoryService.save(feedInventory);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody FeedInventory feedInventory) {
        feedInventory.setId(id);
        return feedInventoryService.updateById(feedInventory);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return feedInventoryService.removeById(id);
    }
}
