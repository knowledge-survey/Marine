package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backed.dto.OperationLogDTO;
import com.backed.entity.OperationLog;
import com.backed.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public List<OperationLogDTO> list(@RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String operationType,
                                      @RequestParam(required = false) String date) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索：搜索用户名或模块或操作
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                .like(OperationLog::getUsername, keyword)
                .or()
                .like(OperationLog::getModule, keyword)
                .or()
                .like(OperationLog::getOperation, keyword)
            );
        }
        
        // 操作类型搜索：对应 operation 字段
        if (operationType != null && !operationType.isEmpty()) {
            wrapper.like(OperationLog::getOperation, operationType);
        }
        
        // 日期搜索
        if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
            wrapper.between(OperationLog::getCreateTime, startOfDay, endOfDay);
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        List<OperationLog> logs = operationLogService.list(wrapper);
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<OperationLogDTO> listAll() {
        List<OperationLog> logs = operationLogService.list();
        return logs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OperationLogDTO getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return convertToDTO(log);
    }

    @PostMapping
    public boolean save(@RequestBody OperationLog operationLog) {
        return operationLogService.save(operationLog);
    }

    @PutMapping
    public boolean updateById(@RequestBody OperationLog operationLog) {
        return operationLogService.updateById(operationLog);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return operationLogService.removeById(id);
    }

    @DeleteMapping
    public boolean removeAll() {
        return operationLogService.remove(new LambdaQueryWrapper<>());
    }
    
    private OperationLogDTO convertToDTO(OperationLog log) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(log.getId());
        dto.setOperator(log.getUsername());
        dto.setOperationType(log.getOperation());
        // 组合 module 和 operation 作为 description
        String desc = log.getModule() != null ? log.getModule() : "";
        if (log.getOperation() != null) {
            desc = desc.isEmpty() ? log.getOperation() : desc + " - " + log.getOperation();
        }
        dto.setDescription(desc);
        dto.setIpAddress(log.getIpAddress());
        dto.setCreateTime(log.getCreateTime());
        return dto;
    }
}
