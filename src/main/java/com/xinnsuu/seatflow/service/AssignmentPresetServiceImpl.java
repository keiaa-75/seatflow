package com.xinnsuu.seatflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.model.enums.AssignmentPresetType;
import com.xinnsuu.seatflow.repository.AcademicStructureRepository;
import com.xinnsuu.seatflow.repository.ClassroomLayoutRepository;
import com.xinnsuu.seatflow.repository.SeatAssignmentRepository;
import com.xinnsuu.seatflow.repository.StudentRepository;

@Service
public class AssignmentPresetServiceImpl implements AssignmentPresetService {

    @Autowired
    private AcademicStructureRepository academicStructureRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassroomLayoutRepository classroomLayoutRepository;

    @Autowired
    private SeatAssignmentRepository seatAssignmentRepository;

    @Override
    public List<SeatAssignment> generateAssignments(Long sectionId, Long layoutId, AssignmentPresetType presetType, String assignmentName) {
        Optional<AcademicStructure> sectionOpt = academicStructureRepository.findById(sectionId);
        if (sectionOpt.isEmpty()) {
            throw new RuntimeException("Section with ID " + sectionId + " not found");
        }

        Optional<ClassroomLayout> layoutOpt = classroomLayoutRepository.findById(layoutId);
        if (layoutOpt.isEmpty()) {
            throw new RuntimeException("Layout with ID " + layoutId + " not found");
        }

        List<Student> students = studentRepository.findByAcademicStructureIdOrderByLastNameAscFirstNameAsc(sectionId);
        if (students.isEmpty()) {
            throw new RuntimeException("No students found in section " + sectionId);
        }

        List<Student> sortedStudents = sortStudents(students, presetType);

        ClassroomLayout layout = layoutOpt.get();
        AcademicStructure section = sectionOpt.get();

        int totalSeats = layout.getRows() * layout.getColumns();
        List<SeatAssignment> assignments = new ArrayList<>();

        for (int i = 0; i < sortedStudents.size() && i < totalSeats; i++) {
            Student student = sortedStudents.get(i);
            int row = (i / layout.getColumns()) + 1;
            int col = (i % layout.getColumns()) + 1;

            if (seatAssignmentRepository.existsByClassroomLayoutAndRowNumberAndColumnNumber(layout, row, col)) {
                continue;
            }

            if (seatAssignmentRepository.existsByStudentAndClassroomLayout(student, layout)) {
                continue;
            }

            SeatAssignment assignment = new SeatAssignment();
            assignment.setStudent(student);
            assignment.setAcademicStructure(section);
            assignment.setClassroomLayout(layout);
            assignment.setAssignmentName(assignmentName);
            assignment.setRowNumber(row);
            assignment.setColumnNumber(col);

            assignments.add(seatAssignmentRepository.save(assignment));
        }

        return assignments;
    }

    private List<Student> sortStudents(List<Student> students, AssignmentPresetType presetType) {
        List<Student> sorted = new ArrayList<>(students);

        switch (presetType) {
            case ALPHABETICAL:
                Collections.sort(sorted, Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName));
                break;
            case REVERSE:
                Collections.sort(sorted, Comparator.comparing(Student::getLastName)
                        .thenComparing(Student::getFirstName));
                Collections.reverse(sorted);
                break;
            case RANDOM:
                Collections.shuffle(sorted);
                break;
        }

        return sorted;
    }
}
