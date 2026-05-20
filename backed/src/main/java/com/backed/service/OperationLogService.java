package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.OperationLog;
import com.backed.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService extends ServiceImpl<OperationLogMapper, OperationLog> {
}
