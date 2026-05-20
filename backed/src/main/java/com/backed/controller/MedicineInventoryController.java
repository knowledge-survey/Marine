package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.MedicineInventory;
import com.backed.service.MedicineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicine-inventory")
public class MedicineInventoryController {

    @Autowired
    private MedicineInventoryService medicineInventoryService;

@GetMapping
    public List<MedicineInventory> list(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String zoneName) {
        LambdaQueryWrapper<MedicineInventory> wrapper = new LambdaQueryWrapper<>();
        if (zoneName != null && !zoneName.isEmpty()) {
            wrapper.eq(MedicineInventory::getZoneName, zoneName);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(MedicineInventory::getMedicineName, keyword).or()
                    .like(MedicineInventory::getSupplier, keyword);
        }
        wrapper.orderByDesc(MedicineInventory::getUpdateTime);
        return medicineInventoryService.list(wrapper);
    }

    @GetMapping("/all")
    public List<MedicineInventory> listAll() {
        return medicineInventoryService.list();
    }

    @GetMapping("/{id}")
    public MedicineInventory getById(@PathVariable Long id) {
        return medicineInventoryService.getById(id);
    }

    @PostMapping
    public boolean save(@RequestBody MedicineInventory medicineInventory) {
        return medicineInventoryService.save(medicineInventory);
    }

    @PutMapping("/{id}")
    public boolean updateById(@PathVariable Long id, @RequestBody MedicineInventory medicineInventory) {
        medicineInventory.setId(id);
        return medicineInventoryService.updateById(medicineInventory);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return medicineInventoryService.removeById(id);
    }
}
