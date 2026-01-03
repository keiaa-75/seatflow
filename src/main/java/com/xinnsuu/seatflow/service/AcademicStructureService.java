package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import com.xinnsuu.seatflow.model.AcademicStructure;

public interface AcademicStructureService {
    List<AcademicStructure> getAllSections();
    List<AcademicStructure> getSectionsByGradeLevel(com.xinnsuu.seatflow.model.enums.GradeLevel gradeLevel);
    Optional<AcademicStructure> getSectionById(Long id);
    AcademicStructure createAcademicStructure(AcademicStructure academicStructure);
    AcademicStructure updateAcademicStructure(Long id, AcademicStructure updatedStructure);
    void deleteAcademicStructure(Long id);
    List<AcademicStructure> searchSections(String query);
    List<AcademicStructure> filterByStrand(List<AcademicStructure> structures, com.xinnsuu.seatflow.model.enums.Strand strand);
}