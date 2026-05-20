package com.backed.service;

import com.backed.entity.AquacultureZone;
import com.backed.entity.WaterQualityThreshold;
import com.backed.mapper.AquacultureZoneMapper;
import com.backed.mapper.WaterQualityThresholdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AquacultureZoneService {

    @Autowired
    private AquacultureZoneMapper aquacultureZoneMapper;

    @Autowired
    private WaterQualityThresholdMapper thresholdMapper;

    public List<AquacultureZone> findAll() {
        return aquacultureZoneMapper.selectList(null);
    }

    public Optional<AquacultureZone> findById(Long id) {
        return Optional.ofNullable(aquacultureZoneMapper.selectById(id));
    }

    public List<AquacultureZone> findRootZones() {
        return aquacultureZoneMapper.findByParentIsNull();
    }

    public List<AquacultureZone> findByParentId(Long parentId) {
        return aquacultureZoneMapper.findByParentId(parentId);
    }

    public List<AquacultureZone> searchByName(String name) {
        return aquacultureZoneMapper.findByNameContaining(name);
    }

    public AquacultureZone save(AquacultureZone zone) {
        aquacultureZoneMapper.insert(zone);
        return zone;
    }

    public AquacultureZone update(AquacultureZone zone) {
        aquacultureZoneMapper.updateById(zone);
        return zone;
    }

    public void deleteById(Long id) {
        aquacultureZoneMapper.deleteById(id);
    }

    public Optional<WaterQualityThreshold> getThreshold(Long zoneId) {
        return thresholdMapper.findByZoneId(zoneId);
    }

    public WaterQualityThreshold saveThreshold(Long zoneId, WaterQualityThreshold threshold) {
        Optional<WaterQualityThreshold> existing = thresholdMapper.findByZoneId(zoneId);
        if (existing.isPresent()) {
            WaterQualityThreshold old = existing.get();
            threshold.setId(old.getId());
            threshold.setZoneId(zoneId);
            thresholdMapper.updateById(threshold);
        } else {
            threshold.setZoneId(zoneId);
            thresholdMapper.insert(threshold);
        }
        return threshold;
    }
}
