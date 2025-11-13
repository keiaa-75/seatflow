package com.xinnsuu.seatflow.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.Student;

public interface StudentCsvParserService {
    List<Student> parseStudents(MultipartFile file, AcademicStructure section);
}