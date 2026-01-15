package com.xinnsuu.seatflow.controller;

import java.util.List;

import com.xinnsuu.seatflow.dto.SectionGroupByStrand;
import com.xinnsuu.seatflow.service.ClassroomLayoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.service.AcademicStructureService;
import com.xinnsuu.seatflow.service.SeatAssignmentService;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/sections")
public class AcademicStructureWebController {

    @Autowired
    private AcademicStructureService academicStructureService;

    @Autowired
    private ClassroomLayoutService classroomLayoutService;
    
    @Autowired
    private SeatAssignmentService seatAssignmentService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String listStructures(@RequestParam(required = false) String grade, Model model) {
        List<SectionGroupByStrand> groupedSections;
        if ("ELEVEN".equals(grade)) {
            groupedSections = academicStructureService.getSectionsGroupedByStrandAndGradeLevel(
                com.xinnsuu.seatflow.model.enums.GradeLevel.ELEVEN);
        } else if ("TWELVE".equals(grade)) {
            groupedSections = academicStructureService.getSectionsGroupedByStrandAndGradeLevel(
                com.xinnsuu.seatflow.model.enums.GradeLevel.TWELVE);
        } else {
            groupedSections = academicStructureService.getSectionsGroupedByStrand();
        }
        model.addAttribute("groupedSections", groupedSections);
        return "sections";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<AcademicStructure>> searchApi(@RequestParam("q") String query) {
        List<AcademicStructure> results = academicStructureService.searchSections(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("structure", new AcademicStructure());
        return "sections-edit";
    }

    @PostMapping("/new")
    public String createStructure(@Valid @ModelAttribute("structure") AcademicStructure structure, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("structure", structure);
            return "sections-edit";
        }
        academicStructureService.createAcademicStructure(structure);
        return "redirect:/sections";
    }

    @GetMapping("/{id}")
    public String showSectionDetail(@PathVariable("id") Long id, Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        // Get counts for dashboard
        int studentCount = academicStructureService.getStudentCountBySection(id);
        int assignmentCount = academicStructureService.getAssignmentCountBySection(id);
        
        model.addAttribute("section", section);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("assignmentCount", assignmentCount);
        
        return "section-detail";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        AcademicStructure structure = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid structure Id:" + id));
        model.addAttribute("structure", structure);
        return "sections-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStructure(@PathVariable("id") Long id, @Valid @ModelAttribute("structure") AcademicStructure structure,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("structure", structure);
            return "sections-edit";
        }
        academicStructureService.updateAcademicStructure(id, structure);
        return "redirect:/sections";
    }

    @PostMapping("/delete/{id}")
    public String deleteStructure(@PathVariable("id") Long id) {
        academicStructureService.deleteAcademicStructure(id);
        return "redirect:/sections";
    }

    // Assignment routes for sections
    @GetMapping("/{id}/assignments/new")
    public String showLayoutSelection(@PathVariable("id") Long id, Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "classroom-layouts";
    }

    @GetMapping("/{id}/assignments/assign")
    public String showAssignmentForm(@PathVariable("id") Long id, 
                                   @RequestParam("layoutType") String layoutType, Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("layoutType", layoutType);
        return "seat-assignment-form";
    }

    @PostMapping("/{id}/assignments/save")
    public String saveAssignment(@PathVariable("id") Long id, 
                               @RequestParam String assignmentName,
                               @RequestParam String assignmentData,
                               @RequestParam String layoutType,
                               @RequestParam(required = false) String assignmentPreset,
                               Model model) {
        try {
            AcademicStructure section = academicStructureService.getSectionById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
            
            // Parse assignment data from JSON string
            @SuppressWarnings("unchecked")
            Map<String, Object> parsedAssignmentData = objectMapper.readValue(assignmentData, Map.class);
            
            // Create new seat assignment
            SeatAssignment assignment = new SeatAssignment();
            assignment.setAssignmentName(assignmentName);
            assignment.setAcademicStructure(section);
            
            // Handle assignment preset if provided
            // TODO: Implement preset handling logic when AssignmentPresetType is available
            if (assignmentPreset != null && !assignmentPreset.isEmpty()) {
                // For now, just log it or add to description
                assignment.setDescription("Preset: " + assignmentPreset);
            }
            
            // Save the assignment
            seatAssignmentService.createAssignmentForSection(id, assignment);
            
            return "redirect:/sections/" + id;
            
        } catch (Exception e) {
            // Add error to model and return to form
            model.addAttribute("error", "Failed to save assignment: " + e.getMessage());
            model.addAttribute("sectionId", id);
            model.addAttribute("layoutType", layoutType);
            return "seat-assignment-form";
        }
    }
}
