package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.RevenueRecord;
import com.backed.mapper.RevenueRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class RevenueRecordService extends ServiceImpl<RevenueRecordMapper, RevenueRecord> {
}
