package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public List<Student> getStudentsBySectionId(Long sectionId) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        return studentRepository.findByAcademicStructureId(sectionId);
    }

    @Override
    public Optional<Student> getStudentById(Long sectionId, String id) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        return studentRepository.findByStudentIdAndAcademicStructureId(id, sectionId);
    }

    @Override
    public Student createStudent(Long sectionId, Student student) {
        Optional<AcademicStructure> sectionOpt = academicStructureRepository.findById(sectionId);

        if (sectionOpt.isEmpty()) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }

        student.setAcademicStructure(sectionOpt.get());
        return studentRepository.save(student);
    }

    @Override
    public void importStudentsFromCsv(Long sectionId, MultipartFile file) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        // Placeholder for CSV import logic
    }

    @Override
    public Student updateStudent(Long sectionId, String id, Student updatedStudent) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        Optional<Student> existingStudentOpt = studentRepository.findByStudentIdAndAcademicStructureId(id, sectionId);

        if (existingStudentOpt.isPresent()) {
            Student existingStudent = existingStudentOpt.get();

            existingStudent.setFirstName(updatedStudent.getFirstName());
            existingStudent.setLastName(updatedStudent.getLastName());
            
            // The studentId (PK) and academicStructure should not be changed here

            return studentRepository.save(existingStudent);
        } else {
            throw new RuntimeException("Student with ID " + id + " not found in section " + sectionId);
        }
    }

    @Override
    public void deleteStudent(Long sectionId, String id) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        if (!studentRepository.findByStudentIdAndAcademicStructureId(id, sectionId).isPresent()) {
            throw new RuntimeException("Student with ID " + id + " not found in section " + sectionId);
        }

        studentRepository.deleteById(id);
    }
}