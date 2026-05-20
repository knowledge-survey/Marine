package com.backed.controller;

import com.backed.entity.DiseaseRecord;
import com.backed.service.DiseaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseRecordController {

    @Autowired
    private DiseaseRecordService diseaseRecordService;

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<DiseaseRecord>> getByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(diseaseRecordService.findByZoneId(zoneId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DiseaseRecord>> getActive() {
        return ResponseEntity.ok(diseaseRecordService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiseaseRecord> getById(@PathVariable Long id) {
        return diseaseRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DiseaseRecord> create(@RequestBody DiseaseRecord record) {
        return ResponseEntity.ok(diseaseRecordService.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiseaseRecord> update(@PathVariable Long id, @RequestBody DiseaseRecord record) {
        record.setId(id);
        return ResponseEntity.ok(diseaseRecordService.update(record));
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<DiseaseRecord> recover(@PathVariable Long id) {
        return ResponseEntity.ok(diseaseRecordService.recover(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diseaseRecordService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
