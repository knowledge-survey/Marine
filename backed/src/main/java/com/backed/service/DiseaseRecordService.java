package com.backed.service;

import com.backed.entity.DiseaseRecord;
import com.backed.mapper.DiseaseRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DiseaseRecordService {

    @Autowired
    private DiseaseRecordMapper diseaseRecordMapper;

    public List<DiseaseRecord> findByZoneId(Long zoneId) {
        return diseaseRecordMapper.findByZoneIdOrderByOccurrenceDateDesc(zoneId);
    }

    public List<DiseaseRecord> findActive() {
        return diseaseRecordMapper.findByStatus(DiseaseRecord.DiseaseStatus.ACTIVE);
    }

    public Optional<DiseaseRecord> findById(Long id) {
        return Optional.ofNullable(diseaseRecordMapper.selectById(id));
    }

    public DiseaseRecord save(DiseaseRecord record) {
        diseaseRecordMapper.insert(record);
        return record;
    }

    public DiseaseRecord update(DiseaseRecord record) {
        diseaseRecordMapper.updateById(record);
        return record;
    }

    public void deleteById(Long id) {
        diseaseRecordMapper.deleteById(id);
    }

    public DiseaseRecord recover(Long id) {
        DiseaseRecord record = findById(id).orElseThrow(() -> new RuntimeException("病害记录不存在"));
        record.setStatus(DiseaseRecord.DiseaseStatus.RECOVERED);
        diseaseRecordMapper.updateById(record);
        return record;
    }
}
