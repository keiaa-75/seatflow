package com.xinnsuu.seatflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xinnsuu.seatflow.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByAcademicStructureIdOrderByLastNameAscFirstNameAsc(Long academicStructureId);
    Optional<Student> findByStudentIdAndAcademicStructureId(String studentId, Long academicStructureId);
}