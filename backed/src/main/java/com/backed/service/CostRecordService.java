package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.CostRecord;
import com.backed.mapper.CostRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class CostRecordService extends ServiceImpl<CostRecordMapper, CostRecord> {
}
