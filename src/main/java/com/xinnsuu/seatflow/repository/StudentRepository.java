package com.xinnsuu.seatflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xinnsuu.seatflow.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    List<Student> findByAcademicStructureIdOrderByLastNameAscFirstNameAsc(Long academicStructureId);
    Optional<Student> findByStudentIdAndAcademicStructureId(String studentId, Long academicStructureId);

    @Query("SELECT s FROM Student s WHERE s.academicStructure.id = :sectionId " +
           "AND s.studentId NOT IN " +
           "(SELECT a.student.studentId FROM SeatAssignment a WHERE a.classroomLayout.id = :layoutId)")
    List<Student> findUnassignedStudentsBySectionAndLayout(
            @Param("sectionId") Long sectionId,
            @Param("layoutId") Long layoutId);
}