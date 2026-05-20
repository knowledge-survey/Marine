package com.backed.controller;

import com.backed.entity.AquacultureZone;
import com.backed.entity.WaterQualityThreshold;
import com.backed.service.AquacultureZoneService;
import com.backed.service.OperationLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class AquacultureZoneController {

    @Autowired
    private AquacultureZoneService aquacultureZoneService;
    
    @Autowired
    private OperationLogHelper operationLogHelper;

    @GetMapping
    public ResponseEntity<List<AquacultureZone>> getAllZones() {
        return ResponseEntity.ok(aquacultureZoneService.findAll());
    }

    @GetMapping("/roots")
    public ResponseEntity<List<AquacultureZone>> getRootZones() {
        return ResponseEntity.ok(aquacultureZoneService.findRootZones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AquacultureZone> getZoneById(@PathVariable Long id) {
        return aquacultureZoneService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AquacultureZone>> searchZones(@RequestParam String name) {
        return ResponseEntity.ok(aquacultureZoneService.searchByName(name));
    }

    @PostMapping
    public ResponseEntity<AquacultureZone> createZone(@RequestBody AquacultureZone zone) {
        ResponseEntity<AquacultureZone> response = ResponseEntity.ok(aquacultureZoneService.save(zone));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("区域管理", "新增养殖区域", "POST", "name=" + zone.getName());
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<AquacultureZone> updateZone(@PathVariable Long id, @RequestBody AquacultureZone zone) {
        if (!aquacultureZoneService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        zone.setId(id);
        ResponseEntity<AquacultureZone> response = ResponseEntity.ok(aquacultureZoneService.update(zone));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("区域管理", "更新养殖区域", "PUT", "id=" + id + ", name=" + zone.getName());
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        aquacultureZoneService.deleteById(id);
        operationLogHelper.log("区域管理", "删除养殖区域", "DELETE", "id=" + id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/threshold")
    public ResponseEntity<WaterQualityThreshold> getThreshold(@PathVariable Long id) {
        return aquacultureZoneService.getThreshold(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/threshold")
    public ResponseEntity<WaterQualityThreshold> saveThreshold(
            @PathVariable Long id,
            @RequestBody WaterQualityThreshold threshold) {
        ResponseEntity<WaterQualityThreshold> response = ResponseEntity.ok(aquacultureZoneService.saveThreshold(id, threshold));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("区域管理", "配置水质阈值", "POST", "zoneId=" + id);
        }
        return response;
    }
}
