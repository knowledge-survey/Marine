package com.backed.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backed.entity.Notification;
import com.backed.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends ServiceImpl<NotificationMapper, Notification> {
}
