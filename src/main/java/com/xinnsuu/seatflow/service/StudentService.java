package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import com.xinnsuu.seatflow.model.Student;

public interface StudentService {
    List<Student> getStudentsBySectionId(Long sectionId);
    Optional<Student> getStudentById(Long sectionId, String id);
    Student createStudent(Long sectionId, Student student);
    Student updateStudent(Long sectionId, String id, Student updatedStudent);
    void deleteStudent(Long sectionId, String id);
}