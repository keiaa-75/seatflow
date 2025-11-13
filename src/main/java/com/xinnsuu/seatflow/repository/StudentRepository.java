package com.xinnsuu.seatflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xinnsuu.seatflow.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // Spring Data JPA automatically provides basic CRUD methods
    List<Student> findByAcademicStructureId(Long academicStructureId);
}