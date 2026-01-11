package com.xinnsuu.seatflow.dto;

import java.util.List;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.enums.GradeLevel;

public class SectionGroupByGrade {
    private GradeLevel gradeLevel;
    private List<AcademicStructure> sections;
    private boolean hasSections;

    public SectionGroupByGrade() {}

    public SectionGroupByGrade(GradeLevel gradeLevel, List<AcademicStructure> sections) {
        this.gradeLevel = gradeLevel;
        this.sections = sections;
        this.hasSections = sections != null && !sections.isEmpty();
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(GradeLevel gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public List<AcademicStructure> getSections() {
        return sections;
    }

    public void setSections(List<AcademicStructure> sections) {
        this.sections = sections;
        this.hasSections = sections != null && !sections.isEmpty();
    }

    public boolean isHasSections() {
        return hasSections;
    }

    public void setHasSections(boolean hasSections) {
        this.hasSections = hasSections;
    }

    public String getDisplayName() {
        return gradeLevel != null ? gradeLevel.getDisplayValue() : "";
    }
}