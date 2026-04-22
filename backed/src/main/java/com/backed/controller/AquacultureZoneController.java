package com.backed.controller;

import com.backed.entity.AquacultureZone;
import com.backed.service.AquacultureZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "*")
public class AquacultureZoneController {

    @Autowired
    private AquacultureZoneService aquacultureZoneService;

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
        return ResponseEntity.ok(aquacultureZoneService.save(zone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AquacultureZone> updateZone(@PathVariable Long id, @RequestBody AquacultureZone zone) {
        if (!aquacultureZoneService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        zone.setId(id);
        return ResponseEntity.ok(aquacultureZoneService.update(zone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        aquacultureZoneService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
