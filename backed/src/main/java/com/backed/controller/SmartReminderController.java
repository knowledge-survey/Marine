package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.entity.SmartReminder;
import com.backed.service.SmartReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/smart-reminders")
public class SmartReminderController {

    @Autowired
    private SmartReminderService smartReminderService;

    @GetMapping
    public Page<SmartReminder> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String reminderType,
                                    @RequestParam(required = false) Boolean isEnabled,
                                    @RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<SmartReminder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SmartReminder::getUserId, userId);
        }
        if (reminderType != null && !reminderType.isEmpty()) {
            wrapper.eq(SmartReminder::getReminderType, reminderType);
        }
        if (isEnabled != null) {
            wrapper.eq(SmartReminder::getIsEnabled, isEnabled);
        }
        wrapper.orderByAsc(SmartReminder::getReminderDate).orderByAsc(SmartReminder::getReminderTime);
        return smartReminderService.page(new Page<>(page, size), wrapper);
    }

    @GetMapping("/all")
    public List<SmartReminder> listAll(@RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<SmartReminder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SmartReminder::getUserId, userId);
        }
        wrapper.orderByAsc(SmartReminder::getReminderDate).orderByAsc(SmartReminder::getReminderTime);
        return smartReminderService.list(wrapper);
    }

    @GetMapping("/due")
    public List<SmartReminder> getDueReminders(@RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<SmartReminder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(SmartReminder::getUserId, userId);
        }
        wrapper.eq(SmartReminder::getIsEnabled, true);
        wrapper.orderByAsc(SmartReminder::getReminderDate).orderByAsc(SmartReminder::getReminderTime);
        
        List<SmartReminder> allReminders = smartReminderService.list(wrapper);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        // 过滤出真正到期的提醒
        return allReminders.stream().filter(reminder -> {
            try {
                // 检查日期和时间是否都已到达
                java.time.LocalDate reminderDate = reminder.getReminderDate();
                String reminderTimeStr = reminder.getReminderTime();
                
                if (reminderDate == null || reminderTimeStr == null) {
                    return false;
                }
                
                // 解析时间
                java.time.LocalTime reminderTime = java.time.LocalTime.parse(reminderTimeStr);
                java.time.LocalDateTime reminderDateTime = java.time.LocalDateTime.of(reminderDate, reminderTime);
                
                // 如果提醒时间还没到，跳过
                if (reminderDateTime.isAfter(now)) {
                    return false;
                }
                
                // 检查是否已经读取（仅ONCE类型需要读取后消失）
                if ("ONCE".equals(reminder.getRepeatType()) && reminder.getLastTriggerTime() != null) {
                    return false;
                }
                
                return true;
            } catch (Exception e) {
                return false;
            }
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    public SmartReminder getById(@PathVariable Long id) {
        return smartReminderService.getById(id);
    }

    @PostMapping
    public SmartReminder save(@RequestBody SmartReminder reminder) {
        reminder.setCreateTime(LocalDateTime.now());
        reminder.setUpdateTime(LocalDateTime.now());
        reminder.setIsEnabled(reminder.getIsEnabled() != null ? reminder.getIsEnabled() : true);
        smartReminderService.save(reminder);
        return reminder;
    }

    @PutMapping
    public boolean updateById(@RequestBody SmartReminder reminder) {
        reminder.setUpdateTime(LocalDateTime.now());
        return smartReminderService.updateById(reminder);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return smartReminderService.removeById(id);
    }

    @PutMapping("/{id}/trigger")
    public boolean triggerReminder(@PathVariable Long id) {
        SmartReminder reminder = smartReminderService.getById(id);
        if (reminder != null) {
            reminder.setLastTriggerTime(LocalDateTime.now());
            return smartReminderService.updateById(reminder);
        }
        return false;
    }

    @PutMapping("/{id}/dismiss")
    public boolean dismissReminder(@PathVariable Long id) {
        SmartReminder reminder = smartReminderService.getById(id);
        if (reminder != null) {
            if ("ONCE".equals(reminder.getRepeatType())) {
                reminder.setIsEnabled(false);
            } else if ("DAILY".equals(reminder.getRepeatType())) {
                // 每天重复：日期加一天
                reminder.setReminderDate(reminder.getReminderDate().plusDays(1));
            } else if ("WEEKLY".equals(reminder.getRepeatType())) {
                // 每周重复：日期加7天
                reminder.setReminderDate(reminder.getReminderDate().plusWeeks(1));
            }
            reminder.setLastTriggerTime(LocalDateTime.now());
            return smartReminderService.updateById(reminder);
        }
        return false;
    }
}