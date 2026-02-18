package com.xinnsuu.seatflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.model.enums.AssignmentPresetType;
import com.xinnsuu.seatflow.repository.ClassroomLayoutRepository;
import com.xinnsuu.seatflow.repository.StudentRepository;

@Service
public class AssignmentPresetServiceImpl implements AssignmentPresetService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassroomLayoutRepository classroomLayoutRepository;

    @Override
    public Map<String, String> generateAssignments(Long sectionId, Long layoutId, AssignmentPresetType presetType) {
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

        int totalSeats = layout.getRows() * layout.getColumns();
        Map<String, String> assignments = new HashMap<>();

        for (int i = 0; i < sortedStudents.size() && i < totalSeats; i++) {
            Student student = sortedStudents.get(i);
            int row = (i / layout.getColumns()) + 1;
            int col = (i % layout.getColumns()) + 1;
            String seatId = row + "-" + col;
            
            assignments.put(seatId, student.getStudentId());
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
