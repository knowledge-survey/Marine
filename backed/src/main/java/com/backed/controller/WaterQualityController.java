package com.backed.controller;

import com.backed.entity.WaterQuality;
import com.backed.entity.WaterQualityThreshold;
import com.backed.service.OperationLogHelper;
import com.backed.service.WaterQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/water-quality")
public class WaterQualityController {

    @Autowired
    private WaterQualityService waterQualityService;
    
    @Autowired
    private OperationLogHelper operationLogHelper;

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<WaterQuality>> getByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(waterQualityService.findByZoneId(zoneId));
    }

    @GetMapping("/zone/{zoneId}/range")
    public ResponseEntity<List<WaterQuality>> getByZoneIdAndRange(
            @PathVariable Long zoneId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(waterQualityService.findByZoneIdAndDateRange(zoneId, startTime, endTime));
    }

    @GetMapping("/zone/{zoneId}/latest")
    public ResponseEntity<WaterQuality> getLatestByZoneId(@PathVariable Long zoneId) {
        WaterQuality latest = waterQualityService.findLatestByZoneId(zoneId);
        return latest != null ? ResponseEntity.ok(latest) : ResponseEntity.notFound().build();
    }

    @GetMapping("/warnings")
    public ResponseEntity<List<WaterQuality>> getAllWarnings() {
        return ResponseEntity.ok(waterQualityService.findAllWarnings());
    }

    @GetMapping("/threshold/{zoneId}")
    public ResponseEntity<WaterQualityThreshold> getThreshold(@PathVariable Long zoneId) {
        return waterQualityService.getThresholdByZoneId(zoneId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WaterQuality> createWaterQuality(@RequestBody WaterQuality waterQuality) {
        ResponseEntity<WaterQuality> response = ResponseEntity.ok(waterQualityService.save(waterQuality));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("水质管理", "新增水质数据", "POST", "zoneId=" + waterQuality.getZoneId());
        }
        return response;
    }

    @PostMapping("/threshold")
    public ResponseEntity<WaterQualityThreshold> createThreshold(@RequestBody WaterQualityThreshold threshold) {
        ResponseEntity<WaterQualityThreshold> response = ResponseEntity.ok(waterQualityService.saveThreshold(threshold));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("水质管理", "配置水质阈值", "POST", "zoneId=" + threshold.getZoneId());
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterQuality> updateWaterQuality(@PathVariable Long id, @RequestBody WaterQuality waterQuality) {
        waterQuality.setId(id);
        ResponseEntity<WaterQuality> response = ResponseEntity.ok(waterQualityService.update(waterQuality));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("水质管理", "更新水质数据", "PUT", "id=" + id);
        }
        return response;
    }

    @PutMapping("/threshold/{id}")
    public ResponseEntity<WaterQualityThreshold> updateThreshold(@PathVariable Long id, @RequestBody WaterQualityThreshold threshold) {
        threshold.setId(id);
        ResponseEntity<WaterQualityThreshold> response = ResponseEntity.ok(waterQualityService.saveThreshold(threshold));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("水质管理", "更新水质阈值", "PUT", "id=" + id);
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterQuality(@PathVariable Long id) {
        waterQualityService.deleteById(id);
        operationLogHelper.log("水质管理", "删除水质数据", "DELETE", "id=" + id);
        return ResponseEntity.ok().build();
    }
}
