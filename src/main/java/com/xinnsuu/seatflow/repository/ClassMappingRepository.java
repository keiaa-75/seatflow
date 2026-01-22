package com.xinnsuu.seatflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xinnsuu.seatflow.model.ClassMapping;

@Repository
public interface ClassMappingRepository extends JpaRepository<ClassMapping, Long> {
    List<ClassMapping> findBySectionId(Long sectionId);
    
    Optional<ClassMapping> findByIdAndSectionId(Long id, Long sectionId);
    
    List<ClassMapping> findBySectionIdAndLayoutId(Long sectionId, String layoutId);
}
