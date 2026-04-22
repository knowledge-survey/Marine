package com.backed.service;

import com.backed.entity.HarvestRecord;
import com.backed.mapper.HarvestRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HarvestRecordService {

    @Autowired
    private HarvestRecordMapper harvestRecordMapper;

    public List<HarvestRecord> findByZoneId(Long zoneId) {
        return harvestRecordMapper.findByZoneIdOrderByHarvestDateDesc(zoneId);
    }

    public Optional<HarvestRecord> findById(Long id) {
        return Optional.ofNullable(harvestRecordMapper.selectById(id));
    }

    public HarvestRecord save(HarvestRecord record) {
        harvestRecordMapper.insert(record);
        return record;
    }

    public HarvestRecord update(HarvestRecord record) {
        harvestRecordMapper.updateById(record);
        return record;
    }

    public void deleteById(Long id) {
        harvestRecordMapper.deleteById(id);
    }
}
