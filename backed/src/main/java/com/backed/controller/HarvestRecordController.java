package com.backed.controller;

import com.backed.entity.HarvestRecord;
import com.backed.service.HarvestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/harvest")
@CrossOrigin(origins = "*")
public class HarvestRecordController {

    @Autowired
    private HarvestRecordService harvestRecordService;

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<HarvestRecord>> getByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(harvestRecordService.findByZoneId(zoneId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HarvestRecord> getById(@PathVariable Long id) {
        return harvestRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HarvestRecord> create(@RequestBody HarvestRecord record) {
        return ResponseEntity.ok(harvestRecordService.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HarvestRecord> update(@PathVariable Long id, @RequestBody HarvestRecord record) {
        record.setId(id);
        return ResponseEntity.ok(harvestRecordService.update(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        harvestRecordService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
