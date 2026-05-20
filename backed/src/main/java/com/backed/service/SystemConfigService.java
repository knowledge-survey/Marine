package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.SystemConfig;
import com.backed.mapper.SystemConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService extends ServiceImpl<SystemConfigMapper, SystemConfig> {
}