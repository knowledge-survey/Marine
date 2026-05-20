package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.SalesRecord;
import com.backed.mapper.SalesRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class SalesRecordService extends ServiceImpl<SalesRecordMapper, SalesRecord> {
}
