package com.backed.controller;

import com.backed.entity.SeedlingRecord;
import com.backed.service.SeedlingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seedlings")
@CrossOrigin(origins = "*")
public class SeedlingRecordController {

    @Autowired
    private SeedlingRecordService seedlingRecordService;

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<SeedlingRecord>> getByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(seedlingRecordService.findByZoneId(zoneId));
    }

    @GetMapping("/batch/{batchNo}")
    public ResponseEntity<List<SeedlingRecord>> getByBatchNo(@PathVariable String batchNo) {
        return ResponseEntity.ok(seedlingRecordService.findByBatchNo(batchNo));
    }

    @GetMapping("/batches")
    public ResponseEntity<List<SeedlingRecord>> getAllBatches() {
        return ResponseEntity.ok(seedlingRecordService.findAllBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeedlingRecord> getById(@PathVariable Long id) {
        return seedlingRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SeedlingRecord> create(@RequestBody SeedlingRecord record) {
        return ResponseEntity.ok(seedlingRecordService.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeedlingRecord> update(@PathVariable Long id, @RequestBody SeedlingRecord record) {
        record.setId(id);
        return ResponseEntity.ok(seedlingRecordService.update(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seedlingRecordService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
