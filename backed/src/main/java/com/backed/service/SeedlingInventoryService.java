package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.SeedlingInventory;
import com.backed.mapper.SeedlingInventoryMapper;
import org.springframework.stereotype.Service;

@Service
public class SeedlingInventoryService extends ServiceImpl<SeedlingInventoryMapper, SeedlingInventory> {
}
