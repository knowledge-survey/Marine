package com.backed.controller;

import com.backed.entity.FeedingRecord;
import com.backed.service.FeedingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeding")
@CrossOrigin(origins = "*")
public class FeedingRecordController {

    @Autowired
    private FeedingRecordService feedingRecordService;

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<FeedingRecord>> getByZoneId(@PathVariable Long zoneId) {
        return ResponseEntity.ok(feedingRecordService.findByZoneId(zoneId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedingRecord> getById(@PathVariable Long id) {
        return feedingRecordService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FeedingRecord> create(@RequestBody FeedingRecord record) {
        return ResponseEntity.ok(feedingRecordService.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedingRecord> update(@PathVariable Long id, @RequestBody FeedingRecord record) {
        record.setId(id);
        return ResponseEntity.ok(feedingRecordService.update(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedingRecordService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
