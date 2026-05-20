package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.MedicineInventory;
import com.backed.mapper.MedicineInventoryMapper;
import org.springframework.stereotype.Service;

@Service
public class MedicineInventoryService extends ServiceImpl<MedicineInventoryMapper, MedicineInventory> {
}
