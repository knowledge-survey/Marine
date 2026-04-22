package com.backed.service;

import com.backed.entity.AquacultureZone;
import com.backed.entity.WaterQuality;
import com.backed.mapper.AquacultureZoneMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
public class WaterQualitySimulatorService {

    @Autowired
    private AquacultureZoneMapper zoneMapper;

    @Autowired
    private WaterQualityService waterQualityService;

    private final Random random = new Random();

    @Scheduled(fixedRate = 30000) // 每30秒生成一次数据
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

        // 生成符合实际范围的随机数据
        waterQuality.setTemperature(generateRandomValue(15, 30, 1));
        waterQuality.setDissolvedOxygen(generateRandomValue(5, 12, 1));
        waterQuality.setPh(generateRandomValue(7, 8.5, 2));
        waterQuality.setSalinity(generateRandomValue(20, 30, 1));

        return waterQuality;
    }

    private BigDecimal generateRandomValue(double min, double max, int scale) {
        double value = min + random.nextDouble() * (max - min);
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }
}
