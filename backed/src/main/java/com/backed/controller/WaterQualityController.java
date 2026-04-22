package com.backed.controller;

import com.backed.entity.WaterQuality;
import com.backed.entity.WaterQualityThreshold;
import com.backed.service.WaterQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/water-quality")
@CrossOrigin(origins = "*")
public class WaterQualityController {

    @Autowired
    private WaterQualityService waterQualityService;

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
        return ResponseEntity.ok(waterQualityService.save(waterQuality));
    }

    @PostMapping("/threshold")
    public ResponseEntity<WaterQualityThreshold> createThreshold(@RequestBody WaterQualityThreshold threshold) {
        return ResponseEntity.ok(waterQualityService.saveThreshold(threshold));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterQuality> updateWaterQuality(@PathVariable Long id, @RequestBody WaterQuality waterQuality) {
        waterQuality.setId(id);
        return ResponseEntity.ok(waterQualityService.update(waterQuality));
    }

    @PutMapping("/threshold/{id}")
    public ResponseEntity<WaterQualityThreshold> updateThreshold(@PathVariable Long id, @RequestBody WaterQualityThreshold threshold) {
        threshold.setId(id);
        return ResponseEntity.ok(waterQualityService.saveThreshold(threshold));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterQuality(@PathVariable Long id) {
        waterQualityService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
