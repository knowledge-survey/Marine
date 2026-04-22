package com.backed.service;

import com.backed.entity.SeedlingRecord;
import com.backed.mapper.SeedlingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeedlingRecordService {

    @Autowired
    private SeedlingRecordMapper seedlingRecordMapper;

    public List<SeedlingRecord> findByZoneId(Long zoneId) {
        return seedlingRecordMapper.findByZoneIdOrderByReleaseDateDesc(zoneId);
    }

    public List<SeedlingRecord> findByBatchNo(String batchNo) {
        return seedlingRecordMapper.findByBatchNo(batchNo);
    }

    public List<SeedlingRecord> findAllBatches() {
        List<String> batchNos = seedlingRecordMapper.findAllBatchNos();
        return batchNos.stream()
                .map(no -> {
                    List<SeedlingRecord> records = seedlingRecordMapper.findByBatchNo(no);
                    return records.isEmpty() ? null : records.get(0);
                })
                .filter(r -> r != null)
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<SeedlingRecord> findById(Long id) {
        return Optional.ofNullable(seedlingRecordMapper.selectById(id));
    }

    public SeedlingRecord save(SeedlingRecord record) {
        seedlingRecordMapper.insert(record);
        return record;
    }

    public SeedlingRecord update(SeedlingRecord record) {
        seedlingRecordMapper.updateById(record);
        return record;
    }

    public void deleteById(Long id) {
        seedlingRecordMapper.deleteById(id);
    }
}
