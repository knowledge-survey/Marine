package com.backed.service;

import com.backed.entity.*;
import com.backed.mapper.*;
import com.backed.util.QrCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TraceabilityCodeService {

    @Autowired
    private TraceabilityCodeMapper traceabilityCodeMapper;

    @Autowired
    private HarvestRecordMapper harvestMapper;

    @Autowired
    private SeedlingRecordMapper seedlingMapper;

    @Autowired
    private FeedingRecordMapper feedingMapper;

    @Autowired
    private WaterQualityMapper waterQualityMapper;

    @Autowired
    private DiseaseRecordMapper diseaseMapper;

    @Autowired
    private AquacultureZoneMapper zoneMapper;

    public List<TraceabilityCode> findAll() {
        return traceabilityCodeMapper.selectList(null);
    }

    public Optional<TraceabilityCode> findById(Long id) {
        return Optional.ofNullable(traceabilityCodeMapper.selectById(id));
    }

    public Optional<TraceabilityCode> findByCode(String code) {
        Optional<TraceabilityCode> codeOpt = traceabilityCodeMapper.findByCode(code);
        if (codeOpt.isPresent()) {
            TraceabilityCode tc = codeOpt.get();
            tc.setScanCount(tc.getScanCount() + 1);
            traceabilityCodeMapper.updateById(tc);
        }
        return codeOpt;
    }

    public Optional<TraceabilityCode> findByHarvestId(Long harvestId) {
        return traceabilityCodeMapper.findByHarvestId(harvestId);
    }

    public Optional<TraceabilityCode> findByBatchNo(String batchNo) {
        return traceabilityCodeMapper.findByBatchNo(batchNo);
    }

    public TraceabilityCode generateCode(TraceabilityCode traceabilityCode) {
        String code = UUID.randomUUID().toString().replace("-", "");
        traceabilityCode.setCode(code);
        traceabilityCodeMapper.insert(traceabilityCode);
        return traceabilityCode;
    }

    public TraceabilityCode generateCodeForHarvest(Long harvestId) {
        Optional<HarvestRecord> harvestOpt = Optional.ofNullable(harvestMapper.selectById(harvestId));
        if (harvestOpt.isEmpty()) {
            throw new RuntimeException("捕捞记录不存在");
        }

        HarvestRecord harvest = harvestOpt.get();
        TraceabilityCode traceabilityCode = new TraceabilityCode();
        traceabilityCode.setHarvestId(harvest.getId());
        traceabilityCode.setZoneId(harvest.getZoneId());
        traceabilityCode.setDescription(harvest.getSpecies() + " - " + harvest.getHarvestDate());
        
        return generateCode(traceabilityCode);
    }

    public TraceabilityCode generateCodeForBatch(String batchNo) {
        List<SeedlingRecord> seedlings = seedlingMapper.findByBatchNo(batchNo);
        if (seedlings.isEmpty()) {
            throw new RuntimeException("苗种批次不存在");
        }

        SeedlingRecord firstSeedling = seedlings.get(0);
        Optional<TraceabilityCode> existing = findByBatchNo(batchNo);
        if (existing.isPresent()) {
            throw new RuntimeException("该批次已生成溯源码: " + existing.get().getCode());
        }

        TraceabilityCode traceabilityCode = new TraceabilityCode();
        traceabilityCode.setBatchNo(batchNo);
        traceabilityCode.setZoneId(firstSeedling.getZoneId());
        traceabilityCode.setDescription(firstSeedling.getSpecies() + " - 批次: " + batchNo);
        
        return generateCode(traceabilityCode);
    }

    public byte[] generateQrCodeImage(String code, String baseUrl) {
        try {
            String qrContent = baseUrl + "/traceability/" + code;
            return QrCodeGenerator.generateQrCode(qrContent);
        } catch (Exception e) {
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    public TraceabilityCode save(TraceabilityCode traceabilityCode) {
        traceabilityCodeMapper.insert(traceabilityCode);
        return traceabilityCode;
    }

    public void deleteById(Long id) {
        traceabilityCodeMapper.deleteById(id);
    }

    public TraceabilityInfo getTraceabilityInfo(String code) {
        Optional<TraceabilityCode> codeOpt = findByCode(code);
        if (codeOpt.isEmpty()) {
            return null;
        }

        TraceabilityCode traceabilityCode = codeOpt.get();
        Long zoneId = traceabilityCode.getZoneId();
        String batchNo = traceabilityCode.getBatchNo();

        if (zoneId == null && batchNo != null) {
            List<SeedlingRecord> seedlings = seedlingMapper.findByBatchNo(batchNo);
            if (!seedlings.isEmpty()) {
                zoneId = seedlings.get(0).getZoneId();
            }
        }

        HarvestRecord harvest = null;
        if (traceabilityCode.getHarvestId() != null) {
            harvest = harvestMapper.selectById(traceabilityCode.getHarvestId());
        }

        AquacultureZone zone = null;
        if (zoneId != null) {
            zone = zoneMapper.selectById(zoneId);
        }

        List<SeedlingRecord> seedlings = null;
        if (batchNo != null) {
            seedlings = seedlingMapper.findByBatchNo(batchNo);
        } else if (zoneId != null) {
            seedlings = seedlingMapper.findByZoneIdOrderByReleaseDateDesc(zoneId);
        }

        List<FeedingRecord> feedings = null;
        if (zoneId != null) {
            feedings = feedingMapper.findByZoneIdOrderByFeedingTimeDesc(zoneId);
        }

        List<WaterQuality> waterQualities = null;
        if (zoneId != null) {
            waterQualities = waterQualityMapper.findByZoneIdOrderByRecordTimeDesc(zoneId);
        }

        List<DiseaseRecord> diseases = null;
        if (zoneId != null) {
            diseases = diseaseMapper.findByZoneIdOrderByOccurrenceDateDesc(zoneId);
        }

        TraceabilityInfo info = new TraceabilityInfo();
        info.setTraceabilityCode(traceabilityCode);
        info.setHarvest(harvest);
        info.setZone(zone);
        info.setSeedlings(seedlings);
        info.setFeedings(feedings);
        info.setWaterQualities(waterQualities);
        info.setDiseases(diseases);

        return info;
    }

    public static class TraceabilityInfo {
        private TraceabilityCode traceabilityCode;
        private HarvestRecord harvest;
        private AquacultureZone zone;
        private List<SeedlingRecord> seedlings;
        private List<FeedingRecord> feedings;
        private List<WaterQuality> waterQualities;
        private List<DiseaseRecord> diseases;

        public TraceabilityCode getTraceabilityCode() { return traceabilityCode; }
        public void setTraceabilityCode(TraceabilityCode traceabilityCode) { this.traceabilityCode = traceabilityCode; }
        public HarvestRecord getHarvest() { return harvest; }
        public void setHarvest(HarvestRecord harvest) { this.harvest = harvest; }
        public AquacultureZone getZone() { return zone; }
        public void setZone(AquacultureZone zone) { this.zone = zone; }
        public List<SeedlingRecord> getSeedlings() { return seedlings; }
        public void setSeedlings(List<SeedlingRecord> seedlings) { this.seedlings = seedlings; }
        public List<FeedingRecord> getFeedings() { return feedings; }
        public void setFeedings(List<FeedingRecord> feedings) { this.feedings = feedings; }
        public List<WaterQuality> getWaterQualities() { return waterQualities; }
        public void setWaterQualities(List<WaterQuality> waterQualities) { this.waterQualities = waterQualities; }
        public List<DiseaseRecord> getDiseases() { return diseases; }
        public void setDiseases(List<DiseaseRecord> diseases) { this.diseases = diseases; }
    }
}
