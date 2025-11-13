package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.repository.AcademicStructureRepository;
import com.xinnsuu.seatflow.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AcademicStructureRepository academicStructureRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student createStudent(Student student) {
        // When creating a student, we need to fetch the full AcademicStructure entity
        Long sectionId = student.getAcademicStructure().getId();
        Optional<AcademicStructure> sectionOpt = academicStructureRepository.findById(sectionId);

        if (sectionOpt.isEmpty()) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }

        student.setAcademicStructure(sectionOpt.get());
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(String id, Student updatedStudent) {
        Optional<Student> existingStudentOpt = studentRepository.findById(id);

        if (existingStudentOpt.isPresent()) {
            Student existingStudent = existingStudentOpt.get();

            Long newSectionId = updatedStudent.getAcademicStructure().getId();
            Optional<AcademicStructure> newSectionOpt = academicStructureRepository.findById(newSectionId);
            
            if (newSectionOpt.isEmpty()) {
                throw new RuntimeException("Academic Structure with ID " + newSectionId + " not found");
            }

            existingStudent.setFirstName(updatedStudent.getFirstName());
            existingStudent.setLastName(updatedStudent.getLastName());
            existingStudent.setStudentId(updatedStudent.getStudentId());
            existingStudent.setAcademicStructure(newSectionOpt.get()); // Update the link

            return studentRepository.save(existingStudent);
        } else {
            throw new RuntimeException("Student with ID " + id + " not found");
        }
    }

    @Override
    public void deleteStudent(String id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student with ID " + id + " not found");
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> getStudentsBySectionId(Long sectionId) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        return studentRepository.findByAcademicStructureId(sectionId);
    }
}