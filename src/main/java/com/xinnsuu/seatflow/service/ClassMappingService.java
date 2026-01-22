package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.xinnsuu.seatflow.model.ClassMapping;

public interface ClassMappingService {
    List<ClassMapping> getMappingsBySectionId(Long sectionId);
    
    Optional<ClassMapping> getMappingByIdAndSectionId(Long id, Long sectionId);
    
    ClassMapping createMapping(Long sectionId, String name, String layoutId, Map<String, String> assignments);
    
    ClassMapping updateMapping(Long sectionId, Long id, String name, String layoutId, Map<String, String> assignments);
    
    void deleteMapping(Long sectionId, Long id);
    
    Optional<ClassMapping> getMappingBySectionIdAndLayoutId(Long sectionId, String layoutId);
}
