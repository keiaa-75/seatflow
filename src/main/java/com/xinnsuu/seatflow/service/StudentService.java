package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import com.xinnsuu.seatflow.model.Student;

public interface StudentService {
    List<Student> getAllStudents();
    Optional<Student> getStudentById(String id);
    List<Student> getStudentsBySectionId(Long sectionId);
    Student createStudent(Student student);
    Student updateStudent(String id, Student updatedStudent);
    void deleteStudent(String id);
}