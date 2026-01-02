package com.xinnsuu.seatflow.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.service.AcademicStructureService;
import com.xinnsuu.seatflow.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentWebController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AcademicStructureService academicStructureService;

    @GetMapping("/{sectionId}")
    public String listStudents(@PathVariable Long sectionId, Model model) {
        model.addAttribute("students", studentService.getStudentsBySectionId(sectionId));
        model.addAttribute("sectionId", sectionId);
        return "students";
    }

    @GetMapping("/{sectionId}/new")
    public String showCreateForm(@PathVariable Long sectionId, Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("structures", academicStructureService.getAllSections());
        model.addAttribute("sectionId", sectionId);
        return "student-form";
    }

    @PostMapping("/{sectionId}/new")
    public String createStudent(@PathVariable Long sectionId, @Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "student-form";
        }
        studentService.createStudent(sectionId, student);
        return "redirect:/students/" + sectionId;
    }

    @GetMapping("/{sectionId}/edit/{id}")
    public String showUpdateForm(@PathVariable Long sectionId, @PathVariable String id, Model model) {
        Student student = studentService.getStudentById(sectionId, id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));
        model.addAttribute("student", student);
        model.addAttribute("structures", academicStructureService.getAllSections());
        model.addAttribute("sectionId", sectionId);
        return "student-form";
    }

    @PostMapping("/{sectionId}/edit/{id}")
    public String updateStudent(@PathVariable Long sectionId, @PathVariable String id, @Valid @ModelAttribute("student") Student student,
            BindingResult result) {
        if (result.hasErrors()) {
            return "student-form";
        }
        studentService.updateStudent(sectionId, id, student);
        return "redirect:/students/" + sectionId;
    }

    @GetMapping("/{sectionId}/delete/{id}")
    public String deleteStudent(@PathVariable Long sectionId, @PathVariable String id) {
        studentService.deleteStudent(sectionId, id);
        return "redirect:/students/" + sectionId;
    }
}
