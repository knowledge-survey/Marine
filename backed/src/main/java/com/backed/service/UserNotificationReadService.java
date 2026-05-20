package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.UserNotificationRead;
import com.backed.mapper.UserNotificationReadMapper;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationReadService extends ServiceImpl<UserNotificationReadMapper, UserNotificationRead> {
}
