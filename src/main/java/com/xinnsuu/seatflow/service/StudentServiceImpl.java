package com.xinnsuu.seatflow.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private StudentCsvParserService studentCsvParserService;

    @Override
    public List<Student> getStudentsBySectionId(Long sectionId) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        return studentRepository.findByAcademicStructureIdOrderByLastNameAscFirstNameAsc(sectionId);
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
        AcademicStructure section = academicStructureRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Academic Structure with ID " + sectionId + " not found"));

        List<Student> studentsToImport = studentCsvParserService.parseStudents(file, section);
        
        if (studentsToImport != null && !studentsToImport.isEmpty()) {
            studentRepository.saveAll(studentsToImport);
        } else {
            System.out.println("No valid student entries found in file or file was empty.");
        }
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

    @Override
    public List<Student> getUnassignedStudentsBySectionAndLayout(Long sectionId, Long layoutId) {
        if (!academicStructureRepository.existsById(sectionId)) {
            throw new RuntimeException("Academic Structure with ID " + sectionId + " not found");
        }
        return studentRepository.findUnassignedStudentsBySectionAndLayout(sectionId, layoutId);
    }

    @Override
    public List<Student> searchStudents(Long sectionId, String query) {
        String lowerQuery = query.toLowerCase();
        return studentRepository.findByAcademicStructureIdOrderByLastNameAscFirstNameAsc(sectionId).stream()
                .filter(s -> s.getDisplayName().toLowerCase().contains(lowerQuery) ||
                        s.getStudentId().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}