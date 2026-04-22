package com.backed.service;

import com.backed.entity.AquacultureZone;
import com.backed.mapper.AquacultureZoneMapper;
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
}
