package com.backed.controller;

import com.backed.entity.TraceabilityCode;
import com.backed.service.TraceabilityCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traceability")
@CrossOrigin(origins = "*")
public class TraceabilityCodeController {

    @Autowired
    private TraceabilityCodeService traceabilityCodeService;

    @Value("${traceability.frontend-url:http://localhost:8080}")
    private String frontendUrl;

    @GetMapping
    public ResponseEntity<List<TraceabilityCode>> getAll() {
        return ResponseEntity.ok(traceabilityCodeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TraceabilityCode> getById(@PathVariable Long id) {
        return traceabilityCodeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TraceabilityCode> getByCode(@PathVariable String code) {
        return traceabilityCodeService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/info/{code}")
    public ResponseEntity<TraceabilityCodeService.TraceabilityInfo> getTraceabilityInfo(@PathVariable String code) {
        TraceabilityCodeService.TraceabilityInfo info = traceabilityCodeService.getTraceabilityInfo(code);
        return info != null ? ResponseEntity.ok(info) : ResponseEntity.notFound().build();
    }

    @GetMapping("/public/{code}")
    public ResponseEntity<TraceabilityCodeService.TraceabilityInfo> getPublicTraceabilityInfo(@PathVariable String code) {
        TraceabilityCodeService.TraceabilityInfo info = traceabilityCodeService.getTraceabilityInfo(code);
        return info != null ? ResponseEntity.ok(info) : ResponseEntity.notFound().build();
    }

    @GetMapping("/qrcode/{code}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String code) {
        try {
            byte[] image = traceabilityCodeService.generateQrCodeImage(code, frontendUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-store, no-cache, must-revalidate");
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TraceabilityCode> generate(@RequestBody TraceabilityCode traceabilityCode) {
        return ResponseEntity.ok(traceabilityCodeService.generateCode(traceabilityCode));
    }

    @PostMapping("/harvest/{harvestId}")
    public ResponseEntity<TraceabilityCode> generateForHarvest(@PathVariable Long harvestId) {
        try {
            TraceabilityCode code = traceabilityCodeService.generateCodeForHarvest(harvestId);
            return ResponseEntity.ok(code);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/batch/{batchNo}")
    public ResponseEntity<TraceabilityCode> generateForBatch(@PathVariable String batchNo) {
        try {
            TraceabilityCode code = traceabilityCodeService.generateCodeForBatch(batchNo);
            return ResponseEntity.ok(code);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        traceabilityCodeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
