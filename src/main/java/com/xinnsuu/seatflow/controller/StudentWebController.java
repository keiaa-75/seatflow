package com.xinnsuu.seatflow.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.service.AcademicStructureService;
import com.xinnsuu.seatflow.service.StudentService;

@Controller
@RequestMapping("/sections/{sectionId}/students")
public class StudentWebController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AcademicStructureService academicStructureService;

    @GetMapping
    public String listStudents(@PathVariable Long sectionId, Model model) {
        model.addAttribute("students", studentService.getStudentsBySectionId(sectionId));
        model.addAttribute("sectionId", sectionId);
        return "students";
    }

    @GetMapping("/new")
    public String showCreateForm(@PathVariable Long sectionId, Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("structures", academicStructureService.getAllSections());
        model.addAttribute("sectionId", sectionId);
        return "student-form";
    }

    @PostMapping("/new")
    public String createStudent(@PathVariable Long sectionId, @Valid @ModelAttribute("student") Student student, 
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "student-form";
        }
        studentService.createStudent(sectionId, student);
        redirectAttributes.addFlashAttribute("successMessage", "Student details have been saved.");
        return "redirect:/sections/" + sectionId + "/students";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long sectionId, @PathVariable String id, Model model) {
        Student student = studentService.getStudentById(sectionId, id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        model.addAttribute("structures", academicStructureService.getAllSections());
        model.addAttribute("sectionId", sectionId);
        return "student-form";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long sectionId, @PathVariable String id, @Valid @ModelAttribute("student") Student student,
            BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "student-form";
        }
        studentService.updateStudent(sectionId, id, student);
        redirectAttributes.addFlashAttribute("successMessage", "Student details have been saved.");
        return "redirect:/sections/" + sectionId + "/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long sectionId, @PathVariable String id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(sectionId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully.");
        return "redirect:/sections/" + sectionId + "/students";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentApi(@PathVariable Long sectionId, @PathVariable String id) {
        try {
            studentService.deleteStudent(sectionId, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/search")
    public ResponseEntity<List<Student>> searchStudentsApi(@PathVariable Long sectionId, @RequestParam("q") String query) {
        List<Student> results = studentService.searchStudents(sectionId, query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/import")
    public String showImportForm(@PathVariable Long sectionId, Model model) {
        model.addAttribute("sectionId", sectionId);
        return "students-import";
    }

    @PostMapping("/import")
    public String importStudents(@PathVariable Long sectionId, @RequestParam("file") MultipartFile file, 
            RedirectAttributes redirectAttributes) {
        try {
            studentService.importStudentsFromCsv(sectionId, file);
            redirectAttributes.addFlashAttribute("successMessage", "Students imported successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error importing students: " + e.getMessage());
        }
        return "redirect:/sections/" + sectionId + "/students";
    }
}
