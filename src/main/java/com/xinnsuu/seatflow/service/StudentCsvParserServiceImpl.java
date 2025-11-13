package com.xinnsuu.seatflow.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.Student;

@Service
public class StudentCsvParserServiceImpl implements StudentCsvParserService {
    private static final String[] HEADERS = {"studentId", "firstName", "lastName", "middleName", "suffix"};

    @Override
    public List<Student> parseStudents(MultipartFile file, AcademicStructure section) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        List<Student> students = new ArrayList<>();
        
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, csvFormat)) {

            for (CSVRecord csvRecord : csvParser) {
                Student student = new Student();
                
                // Set fields from CSV
                student.setStudentId(csvRecord.get("studentId"));
                student.setFirstName(csvRecord.get("firstName"));
                student.setLastName(csvRecord.get("lastName"));
                
                // Handle optional fields
                if (csvRecord.isMapped("middleName")) {
                    student.setMiddleName(csvRecord.get("middleName"));
                }
                if (csvRecord.isMapped("suffix")) {
                    student.setSuffix(csvRecord.get("suffix"));
                }

                student.setAcademicStructure(section);
                
                students.add(student);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }

        return students;
    }
}