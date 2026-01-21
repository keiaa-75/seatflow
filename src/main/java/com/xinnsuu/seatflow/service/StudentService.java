package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.xinnsuu.seatflow.model.Student;

public interface StudentService {
    List<Student> getStudentsBySectionId(Long sectionId);
    Optional<Student> getStudentById(Long sectionId, String id);
    Student createStudent(Long sectionId, Student student);
    void importStudentsFromCsv(Long sectionId, MultipartFile file);
    Student updateStudent(Long sectionId, String id, Student updatedStudent);
    void deleteStudent(Long sectionId, String id);
    List<Student> getUnassignedStudentsBySectionAndLayout(Long sectionId, Long layoutId);
}