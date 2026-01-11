package com.xinnsuu.seatflow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.dto.SectionGroupByStrand;
import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.enums.GradeLevel;
import com.xinnsuu.seatflow.model.enums.Strand;
import com.xinnsuu.seatflow.repository.AcademicStructureRepository;

@Service
public class AcademicStructureServiceImpl implements AcademicStructureService {

    @Autowired
    private AcademicStructureRepository academicStructureRepository;

    @Override
    public List<AcademicStructure> getAllSections() {
        return academicStructureRepository.findAll();
    }

    @Override
    public List<AcademicStructure> getSectionsByGradeLevel(com.xinnsuu.seatflow.model.enums.GradeLevel gradeLevel) {
        return academicStructureRepository.findAll().stream()
                .filter(s -> s.getGradeLevel() == gradeLevel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AcademicStructure> getSectionById(Long id) {
        return academicStructureRepository.findById(id);
    }

    @Override
    public AcademicStructure createAcademicStructure(AcademicStructure academicStructure) {
        return academicStructureRepository.save(academicStructure);
    }

    @Override
    public AcademicStructure updateAcademicStructure(Long id, AcademicStructure updatedStructure) {
        Optional<AcademicStructure> existingStructureOpt = academicStructureRepository.findById(id);

        if (existingStructureOpt.isPresent()) {
            AcademicStructure existingStructure = existingStructureOpt.get();
            
            existingStructure.setGradeLevel(updatedStructure.getGradeLevel());
            existingStructure.setStrand(updatedStructure.getStrand());
            existingStructure.setSectionName(updatedStructure.getSectionName());
            
            return academicStructureRepository.save(existingStructure);
        } else {
            throw new RuntimeException("Academic Structure with ID " + id + " not found");
        }
    }

    @Override
    public void deleteAcademicStructure(Long id) {
        if (!academicStructureRepository.existsById(id)) {
            throw new RuntimeException("Academic Structure with ID " + id + " not found");
        }
        academicStructureRepository.deleteById(id);
    }

    @Override
    public List<AcademicStructure> searchSections(String query) {
        String lowerQuery = query.toLowerCase();
        return academicStructureRepository.findAll().stream()
                .filter(s -> s.getSectionName().toLowerCase().contains(lowerQuery)
                        || s.getGradeLevel().getDisplayValue().contains(lowerQuery)
                        || s.getStrand().name().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicStructure> filterByStrand(List<AcademicStructure> structures, Strand strand) {
        return structures.stream()
                .filter(s -> s.getStrand() == strand)
                .collect(Collectors.toList());
    }

    @Override
    public List<SectionGroupByStrand> getSectionsGroupedByStrand() {
        List<AcademicStructure> allSections = academicStructureRepository.findAll();
        Map<Strand, List<AcademicStructure>> groupedByStrand = allSections.stream()
                .collect(Collectors.groupingBy(AcademicStructure::getStrand));

        List<SectionGroupByStrand> result = new ArrayList<>();
        for (Strand strand : Strand.values()) {
            List<AcademicStructure> strandSections = groupedByStrand.getOrDefault(strand, new ArrayList<>());
            Map<GradeLevel, List<AcademicStructure>> groupedByGrade = strandSections.stream()
                    .collect(Collectors.groupingBy(AcademicStructure::getGradeLevel));

            result.add(new SectionGroupByStrand(strand, groupedByGrade));
        }

        return result;
    }

    @Override
    public List<SectionGroupByStrand> getSectionsGroupedByStrandAndGradeLevel(GradeLevel gradeLevel) {
        List<AcademicStructure> filteredSections = academicStructureRepository.findAll().stream()
                .filter(s -> s.getGradeLevel() == gradeLevel)
                .collect(Collectors.toList());

        Map<Strand, List<AcademicStructure>> groupedByStrand = filteredSections.stream()
                .collect(Collectors.groupingBy(AcademicStructure::getStrand));

        List<SectionGroupByStrand> result = new ArrayList<>();
        for (Strand strand : Strand.values()) {
            List<AcademicStructure> strandSections = groupedByStrand.getOrDefault(strand, new ArrayList<>());
            Map<GradeLevel, List<AcademicStructure>> groupedByGrade = strandSections.stream()
                    .collect(Collectors.groupingBy(AcademicStructure::getGradeLevel));

            result.add(new SectionGroupByStrand(strand, groupedByGrade));
        }

        return result;
    }

    @Override
    public int getStudentCountBySection(Long sectionId) {
        return academicStructureRepository.findById(sectionId)
                .map(section -> section.getStudents() != null ? section.getStudents().size() : 0)
                .orElse(0);
    }

    @Override
    public int getAssignmentCountBySection(Long sectionId) {
        return academicStructureRepository.findById(sectionId)
                .map(section -> section.getSeatAssignments() != null ? section.getSeatAssignments().size() : 0)
                .orElse(0);
    }
}