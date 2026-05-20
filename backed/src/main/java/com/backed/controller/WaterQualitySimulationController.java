package com.backed.controller;

import com.backed.entity.WaterQualitySimulation;
import com.backed.service.WaterQualitySimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simulation")
public class WaterQualitySimulationController {

    @Autowired
    private WaterQualitySimulationService simulationService;

    @GetMapping
    public ResponseEntity<List<WaterQualitySimulation>> getAllSimulations() {
        return ResponseEntity.ok(simulationService.findAll());
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<WaterQualitySimulation> getByZoneId(@PathVariable Long zoneId) {
        return simulationService.findByZoneId(zoneId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/zone/{zoneId}")
    public ResponseEntity<WaterQualitySimulation> saveSimulation(
            @PathVariable Long zoneId,
            @RequestBody WaterQualitySimulation simulation) {
        return ResponseEntity.ok(simulationService.save(zoneId, simulation));
    }

    @DeleteMapping("/zone/{zoneId}")
    public ResponseEntity<Void> deleteByZoneId(@PathVariable Long zoneId) {
        simulationService.deleteByZoneId(zoneId);
        return ResponseEntity.ok().build();
    }
}
