package com.xinnsuu.seatflow.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.model.ClassMapping;
import com.xinnsuu.seatflow.repository.ClassMappingRepository;

@Service
public class ClassMappingServiceImpl implements ClassMappingService {

    @Autowired
    private ClassMappingRepository classMappingRepository;

    @Override
    public List<ClassMapping> getMappingsBySectionId(Long sectionId) {
        return classMappingRepository.findBySectionId(sectionId);
    }

    @Override
    public Optional<ClassMapping> getMappingByIdAndSectionId(Long id, Long sectionId) {
        return classMappingRepository.findByIdAndSectionId(id, sectionId);
    }

    @Override
    public ClassMapping createMapping(Long sectionId, String name, String layoutId, Map<String, String> assignments) {
        ClassMapping mapping = new ClassMapping();
        mapping.setSectionId(sectionId);
        mapping.setName(name);
        mapping.setLayoutId(layoutId);
        if (assignments != null) {
            mapping.setAssignments(new HashMap<>(assignments));
        } else {
            mapping.setAssignments(new HashMap<>());
        }
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());
        
        return classMappingRepository.save(mapping);
    }

    @Override
    public ClassMapping updateMapping(Long sectionId, Long id, String name, String layoutId, Map<String, String> assignments) {
        Optional<ClassMapping> existingMappingOpt = classMappingRepository.findByIdAndSectionId(id, sectionId);
        
        if (existingMappingOpt.isPresent()) {
            ClassMapping existingMapping = existingMappingOpt.get();
            
            if (name != null) {
                existingMapping.setName(name);
            }
            if (layoutId != null) {
                existingMapping.setLayoutId(layoutId);
            }
            if (assignments != null) {
                existingMapping.setAssignments(new HashMap<>(assignments));
            }
            existingMapping.setUpdatedAt(LocalDateTime.now());
            
            return classMappingRepository.save(existingMapping);
        } else {
            throw new RuntimeException("ClassMapping with ID " + id + " not found for section " + sectionId);
        }
    }

    @Override
    public void deleteMapping(Long sectionId, Long id) {
        Optional<ClassMapping> mappingOpt = classMappingRepository.findByIdAndSectionId(id, sectionId);
        
        if (mappingOpt.isPresent()) {
            classMappingRepository.delete(mappingOpt.get());
        } else {
            throw new RuntimeException("ClassMapping with ID " + id + " not found for section " + sectionId);
        }
    }

    @Override
    public Optional<ClassMapping> getMappingBySectionIdAndLayoutId(Long sectionId, String layoutId) {
        List<ClassMapping> mappings = classMappingRepository.findBySectionIdAndLayoutId(sectionId, layoutId);
        if (mappings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mappings.get(0));
    }
}
