package com.backed.service;

import com.backed.entity.WaterQuality;
import com.backed.entity.WaterQualityThreshold;
import com.backed.mapper.WaterQualityMapper;
import com.backed.mapper.WaterQualityThresholdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WaterQualityService {

    @Autowired
    private WaterQualityMapper waterQualityMapper;

    @Autowired
    private WaterQualityThresholdMapper thresholdMapper;

    public List<WaterQuality> findByZoneId(Long zoneId) {
        return waterQualityMapper.findByZoneIdOrderByRecordTimeDesc(zoneId);
    }

    public List<WaterQuality> findByZoneIdAndDateRange(Long zoneId, LocalDateTime startTime, LocalDateTime endTime) {
        return waterQualityMapper.findByZoneIdAndRecordTimeBetweenOrderByRecordTimeAsc(zoneId, startTime, endTime);
    }

    public WaterQuality findLatestByZoneId(Long zoneId) {
        return waterQualityMapper.findFirstByZoneIdOrderByRecordTimeDesc(zoneId);
    }

    public List<WaterQuality> findAllWarnings() {
        return waterQualityMapper.findByIsWarningTrueOrderByRecordTimeDesc();
    }

    public WaterQuality save(WaterQuality waterQuality) {
        checkAndSetWarning(waterQuality);
        waterQualityMapper.insert(waterQuality);
        return waterQuality;
    }

    public WaterQuality update(WaterQuality waterQuality) {
        checkAndSetWarning(waterQuality);
        waterQualityMapper.updateById(waterQuality);
        return waterQuality;
    }

    public void deleteById(Long id) {
        waterQualityMapper.deleteById(id);
    }

    private void checkAndSetWarning(WaterQuality waterQuality) {
        Optional<WaterQualityThreshold> thresholdOpt = thresholdMapper.findByZoneId(waterQuality.getZoneId());
        if (thresholdOpt.isEmpty()) {
            waterQuality.setIsWarning(false);
            waterQuality.setWarningInfo(null);
            return;
        }

        WaterQualityThreshold threshold = thresholdOpt.get();
        List<String> warnings = new ArrayList<>();

        if (waterQuality.getTemperature() != null) {
            if (threshold.getTempMin() != null && waterQuality.getTemperature().compareTo(threshold.getTempMin()) < 0) {
                warnings.add("水温低于最小值");
            }
            if (threshold.getTempMax() != null && waterQuality.getTemperature().compareTo(threshold.getTempMax()) > 0) {
                warnings.add("水温高于最大值");
            }
        }

        if (waterQuality.getDissolvedOxygen() != null) {
            if (threshold.getDoMin() != null && waterQuality.getDissolvedOxygen().compareTo(threshold.getDoMin()) < 0) {
                warnings.add("溶解氧低于最小值");
            }
            if (threshold.getDoMax() != null && waterQuality.getDissolvedOxygen().compareTo(threshold.getDoMax()) > 0) {
                warnings.add("溶解氧高于最大值");
            }
        }

        if (waterQuality.getPh() != null) {
            if (threshold.getPhMin() != null && waterQuality.getPh().compareTo(threshold.getPhMin()) < 0) {
                warnings.add("pH值低于最小值");
            }
            if (threshold.getPhMax() != null && waterQuality.getPh().compareTo(threshold.getPhMax()) > 0) {
                warnings.add("pH值高于最大值");
            }
        }

        if (waterQuality.getSalinity() != null) {
            if (threshold.getSalinityMin() != null && waterQuality.getSalinity().compareTo(threshold.getSalinityMin()) < 0) {
                warnings.add("盐度低于最小值");
            }
            if (threshold.getSalinityMax() != null && waterQuality.getSalinity().compareTo(threshold.getSalinityMax()) > 0) {
                warnings.add("盐度高于最大值");
            }
        }

        if (!warnings.isEmpty()) {
            waterQuality.setIsWarning(true);
            waterQuality.setWarningInfo(String.join("; ", warnings));
        } else {
            waterQuality.setIsWarning(false);
            waterQuality.setWarningInfo(null);
        }
    }

    public Optional<WaterQualityThreshold> getThresholdByZoneId(Long zoneId) {
        return thresholdMapper.findByZoneId(zoneId);
    }

    public WaterQualityThreshold saveThreshold(WaterQualityThreshold threshold) {
        thresholdMapper.insert(threshold);
        return threshold;
    }
}
