package com.backed.service;

import com.backed.entity.AquacultureZone;
import com.backed.entity.WaterQuality;
import com.backed.entity.WaterQualitySimulation;
import com.backed.mapper.AquacultureZoneMapper;
import com.backed.mapper.WaterQualitySimulationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class WaterQualitySimulatorService {

    @Autowired
    private AquacultureZoneMapper zoneMapper;

    @Autowired
    private WaterQualityService waterQualityService;

    @Autowired
    private WaterQualitySimulationMapper simulationMapper;

    private final Random random = new Random();

    @Scheduled(fixedRate = 30000)
    public void generateWaterQualityData() {
        List<AquacultureZone> zones = zoneMapper.selectList(null);
        for (AquacultureZone zone : zones) {
            WaterQuality waterQuality = generateRandomWaterQuality(zone);
            waterQualityService.save(waterQuality);
        }
    }

    private WaterQuality generateRandomWaterQuality(AquacultureZone zone) {
        WaterQuality waterQuality = new WaterQuality();
        waterQuality.setZoneId(zone.getId());

        Optional<WaterQualitySimulation> simOpt = simulationMapper.findByZoneId(zone.getId());

        if (simOpt.isPresent()) {
            WaterQualitySimulation s = simOpt.get();
            waterQuality.setTemperature(generateRandomInRange(s.getTempMin(), s.getTempMax(), 1));
            waterQuality.setDissolvedOxygen(generateRandomInRange(s.getDoMin(), s.getDoMax(), 1));
            waterQuality.setPh(generateRandomInRange(s.getPhMin(), s.getPhMax(), 2));
            waterQuality.setSalinity(generateRandomInRange(s.getSalinityMin(), s.getSalinityMax(), 1));
        } else {
            waterQuality.setTemperature(generateRandomValue(15, 30, 1));
            waterQuality.setDissolvedOxygen(generateRandomValue(5, 12, 1));
            waterQuality.setPh(generateRandomValue(7, 8.5, 2));
            waterQuality.setSalinity(generateRandomValue(20, 30, 1));
        }

        return waterQuality;
    }

    private BigDecimal generateRandomInRange(BigDecimal min, BigDecimal max, int scale) {
        if (min == null || max == null) {
            return generateRandomValue(15, 30, scale);
        }
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        double value = minVal + random.nextDouble() * (maxVal - minVal);
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal generateRandomValue(double min, double max, int scale) {
        double value = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }
}
