package com.backed.service;

import com.backed.entity.FeedingRecord;
import com.backed.mapper.FeedingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedingRecordService {

    @Autowired
    private FeedingRecordMapper feedingRecordMapper;

    public List<FeedingRecord> findByZoneId(Long zoneId) {
        return feedingRecordMapper.findByZoneIdOrderByFeedingTimeDesc(zoneId);
    }

    public Optional<FeedingRecord> findById(Long id) {
        return Optional.ofNullable(feedingRecordMapper.selectById(id));
    }

    public FeedingRecord save(FeedingRecord record) {
        feedingRecordMapper.insert(record);
        return record;
    }

    public FeedingRecord update(FeedingRecord record) {
        feedingRecordMapper.updateById(record);
        return record;
    }

    public void deleteById(Long id) {
        feedingRecordMapper.deleteById(id);
    }
}
