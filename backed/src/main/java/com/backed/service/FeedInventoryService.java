package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.FeedInventory;
import com.backed.mapper.FeedInventoryMapper;
import org.springframework.stereotype.Service;

@Service
public class FeedInventoryService extends ServiceImpl<FeedInventoryMapper, FeedInventory> {
}
