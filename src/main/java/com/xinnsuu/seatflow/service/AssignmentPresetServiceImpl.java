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

        // Generate available seats (excluding disabled ones)
        List<String> availableSeats = generateAvailableSeats(layout);
        Map<String, String> assignments = new HashMap<>();

        // Assign students to available seats only
        for (int i = 0; i < sortedStudents.size() && i < availableSeats.size(); i++) {
            Student student = sortedStudents.get(i);
            String seatId = availableSeats.get(i);
            assignments.put(seatId, student.getStudentId());
        }

        return assignments;
    }

    private List<String> generateAvailableSeats(ClassroomLayout layout) {
        List<String> availableSeats = new ArrayList<>();
        List<String> disabledSeats = layout.getDisabledSeats();

        for (int row = 1; row <= layout.getRows(); row++) {
            for (int col = 1; col <= layout.getColumns(); col++) {
                String seatId = row + "-" + col;
                // Only add seat if it's not disabled
                if (disabledSeats == null || !disabledSeats.contains(seatId)) {
                    availableSeats.add(seatId);
                }
            }
        }

        return availableSeats;
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
