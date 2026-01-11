package com.xinnsuu.seatflow.dto;

import java.util.List;
import java.util.Map;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.enums.GradeLevel;
import com.xinnsuu.seatflow.model.enums.Strand;

public class SectionGroupByStrand {
    private Strand strand;
    private Map<GradeLevel, List<AcademicStructure>> sectionsByGrade;
    private boolean hasSections;

    public SectionGroupByStrand() {}

    public SectionGroupByStrand(Strand strand, Map<GradeLevel, List<AcademicStructure>> sectionsByGrade) {
        this.strand = strand;
        this.sectionsByGrade = sectionsByGrade;
        this.hasSections = sectionsByGrade != null && sectionsByGrade.values().stream().anyMatch(list -> !list.isEmpty());
    }

    public Strand getStrand() {
        return strand;
    }

    public void setStrand(Strand strand) {
        this.strand = strand;
    }

    public Map<GradeLevel, List<AcademicStructure>> getSectionsByGrade() {
        return sectionsByGrade;
    }

    public void setSectionsByGrade(Map<GradeLevel, List<AcademicStructure>> sectionsByGrade) {
        this.sectionsByGrade = sectionsByGrade;
        this.hasSections = sectionsByGrade != null && sectionsByGrade.values().stream().anyMatch(list -> !list.isEmpty());
    }

    public boolean isHasSections() {
        return hasSections;
    }

    public void setHasSections(boolean hasSections) {
        this.hasSections = hasSections;
    }

    public String getDisplayName() {
        return strand != null ? strand.name().replace("_", " ") : "";
    }
}