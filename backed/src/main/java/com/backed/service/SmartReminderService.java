package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.SmartReminder;
import com.backed.mapper.SmartReminderMapper;
import org.springframework.stereotype.Service;

@Service
public class SmartReminderService extends ServiceImpl<SmartReminderMapper, SmartReminder> {
}
