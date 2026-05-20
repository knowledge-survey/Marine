package com.backed.service;

import com.backed.entity.WaterQualitySimulation;
import com.backed.mapper.WaterQualitySimulationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WaterQualitySimulationService {

    @Autowired
    private WaterQualitySimulationMapper simulationMapper;

    public List<WaterQualitySimulation> findAll() {
        return simulationMapper.selectList(null);
    }

    public Optional<WaterQualitySimulation> findByZoneId(Long zoneId) {
        return simulationMapper.findByZoneId(zoneId);
    }

    public WaterQualitySimulation save(Long zoneId, WaterQualitySimulation simulation) {
        Optional<WaterQualitySimulation> existing = simulationMapper.findByZoneId(zoneId);
        if (existing.isPresent()) {
            WaterQualitySimulation old = existing.get();
            simulation.setId(old.getId());
            simulation.setZoneId(zoneId);
            simulationMapper.updateById(simulation);
        } else {
            simulation.setZoneId(zoneId);
            simulationMapper.insert(simulation);
        }
        return simulation;
    }

    public void deleteByZoneId(Long zoneId) {
        LambdaQueryWrapper<WaterQualitySimulation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WaterQualitySimulation::getZoneId, zoneId);
        simulationMapper.delete(wrapper);
    }
}
