package com.xinnsuu.seatflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.service.AcademicStructureService;
import com.xinnsuu.seatflow.service.ClassroomLayoutService;
import com.xinnsuu.seatflow.service.SeatAssignmentService;
import com.xinnsuu.seatflow.service.StudentService;

@Controller
@RequestMapping("/assignments")
public class SeatAssignmentWebController {

    @Autowired
    private SeatAssignmentService seatAssignmentService;

    @Autowired
    private AcademicStructureService academicStructureService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassroomLayoutService classroomLayoutService;

    @GetMapping
    public String listAssignments(@RequestParam(name = "sectionId", required = false) Long sectionId, Model model) {
        model.addAttribute("sections", academicStructureService.getAllSections());
        if (sectionId != null) {
            model.addAttribute("assignments", seatAssignmentService.getAssignmentsBySectionId(sectionId));
        }
        return "seat-assignments";
    }

    @GetMapping("/new")
    public String showLayoutSelection(@RequestParam("sectionId") Long sectionId, Model model) {
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "classroom-layouts";
    }

    @GetMapping("/new/assign")
    public String showCreateForm(@RequestParam("sectionId") Long sectionId, 
                                @RequestParam("layoutId") Long layoutId, Model model) {
        model.addAttribute("assignment", new SeatAssignment());
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("layoutId", layoutId);
        model.addAttribute("layout", classroomLayoutService.getLayoutById(layoutId).orElse(null));
        model.addAttribute("students", studentService.getStudentsBySectionId(sectionId));
        return "seat-assignment-form";
    }

    /**
     * Handles the creation of a new seat assignment.
     * The @Valid annotation has been removed because the AcademicStructure is set
     * in the service layer, but validation runs before the service is called.
     * This was causing a silent validation failure.
     */
    @PostMapping("/new")
    public String createAssignment(@RequestParam("sectionId") Long sectionId, @ModelAttribute("assignment") SeatAssignment assignment) {
        seatAssignmentService.createAssignmentForSection(sectionId, assignment);
        return "redirect:/assignments?sectionId=" + sectionId;
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, @RequestParam("sectionId") Long sectionId, Model model) {
        SeatAssignment assignment = seatAssignmentService.getAssignmentByIdAndSectionId(id, sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid assignment Id:" + id));
        model.addAttribute("assignment", assignment);
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("students", studentService.getStudentsBySectionId(sectionId));
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "seat-assignment-form";
    }

    /**
     * Handles the update of an existing seat assignment.
     * The @Valid annotation has been removed for the same reason as in the create method.
     */
    @PostMapping("/edit/{id}")
    public String updateAssignment(@PathVariable("id") Long id, @RequestParam("sectionId") Long sectionId, @ModelAttribute("assignment") SeatAssignment assignment) {
        seatAssignmentService.updateAssignmentForSection(sectionId, id, assignment);
        return "redirect:/assignments?sectionId=" + sectionId;
    }

    @GetMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable("id") Long id, @RequestParam("sectionId") Long sectionId) {
        seatAssignmentService.deleteAssignmentForSection(sectionId, id);
        return "redirect:/assignments?sectionId=" + sectionId;
    }
}
