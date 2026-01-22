package com.xinnsuu.seatflow.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xinnsuu.seatflow.dto.SeatGridData;
import com.xinnsuu.seatflow.dto.SectionGroupByStrand;
import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.model.ClassMapping;
import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.model.SeatAssignmentDetailDTO;
import com.xinnsuu.seatflow.model.Student;
import com.xinnsuu.seatflow.service.AcademicStructureService;
import com.xinnsuu.seatflow.service.ClassMappingService;
import com.xinnsuu.seatflow.service.ClassroomLayoutService;
import com.xinnsuu.seatflow.service.SeatAssignmentService;
import com.xinnsuu.seatflow.service.StudentService;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/sections")
public class AcademicStructureWebController {

    @Autowired
    private AcademicStructureService academicStructureService;

    @Autowired
    private ClassroomLayoutService classroomLayoutService;
    
    @Autowired
    private ClassMappingService classMappingService;

    @Autowired
    private SeatAssignmentService seatAssignmentService;

    @Autowired
    private StudentService studentService;
    
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
        
        // Find layout by presetId
        ClassroomLayout layout = classroomLayoutService.getAllLayouts().stream()
                .filter(l -> l.getPresetId() != null && l.getPresetId().equals(layoutType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Layout not found for type: " + layoutType));

        List<Student> students = studentService.getStudentsBySectionId(id);
        List<SeatAssignmentDetailDTO> existingAssignments = 
                seatAssignmentService.getAssignmentDetailsBySectionId(id);

        Map<String, SeatGridData.SeatData> seatAssignments = new HashMap<>();
        for (SeatAssignmentDetailDTO assignment : existingAssignments) {
            if (assignment.getLayoutId().equals(layout.getId())) {
                String seatKey = assignment.getRowNumber() + "-" + assignment.getColumnNumber();
                seatAssignments.put(seatKey, new SeatGridData.SeatData(
                        assignment.getStudentId(),
                        assignment.getStudentName(),
                        getInitials(assignment.getStudentName())
                ));
            }
        }

        List<SeatGridData.SeatInfo> allSeats = new ArrayList<>();
        List<String> disabledSeats = layout.getDisabledSeats() != null ? layout.getDisabledSeats() : new ArrayList<>();

        for (int r = 1; r <= layout.getRows(); r++) {
            for (int c = 1; c <= layout.getColumns(); c++) {
                String seatId = r + "-" + c;
                SeatGridData.SeatInfo seatInfo = new SeatGridData.SeatInfo();
                seatInfo.setId(seatId);
                seatInfo.setRow(r);
                seatInfo.setCol(c);

                if (disabledSeats.contains(seatId)) {
                    seatInfo.setStatus("disabled");
                } else if (seatAssignments.containsKey(seatId)) {
                    seatInfo.setStatus("occupied");
                    SeatGridData.SeatData assigned = seatAssignments.get(seatId);
                    seatInfo.setStudentId(assigned.getStudentId());
                    seatInfo.setStudentName(assigned.getStudentName());
                    seatInfo.setInitials(assigned.getInitials());
                } else {
                    seatInfo.setStatus("available");
                }

                allSeats.add(seatInfo);
            }
        }

        SeatGridData gridData = new SeatGridData(
                id,
                layout.getId(),
                layout.getPresetId() != null ? layout.getPresetId() : layout.getLayoutType().name(),
                layout.getRows(),
                layout.getColumns(),
                disabledSeats,
                students,
                seatAssignments,
                allSeats
        );

        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("layoutType", layoutType);
        model.addAttribute("layoutId", layout.getId());
        model.addAttribute("layout", layout);
        model.addAttribute("students", students);
        model.addAttribute("gridData", gridData);
        
        return "seat-assignment-form";
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String[] parts = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
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

    // ClassMapping routes for Seating Charts
    @GetMapping("/{id}/class-mappings")
    public String showClassMappingsList(@PathVariable("id") Long id, Model model) {
        return "redirect:/sections/" + id + "/class-mappings/list";
    }

    @GetMapping("/{id}/class-mappings/list")
    public String showClassMappingsManage(@PathVariable("id") Long id, Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        
        return "class-mappings";
    }

    @GetMapping("/{id}/class-mappings/new")
    public String showClassMappingLayoutSelection(@PathVariable("id") Long id, Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "class-mapping-layouts";
    }

    @GetMapping("/{id}/class-mappings/new/grid")
    public String showClassMappingGrid(@PathVariable("id") Long id,
                                       @RequestParam("layoutType") String layoutType,
                                       Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        // Find layout by presetId
        ClassroomLayout layout = classroomLayoutService.getAllLayouts().stream()
                .filter(l -> l.getPresetId() != null && l.getPresetId().equals(layoutType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Layout not found for type: " + layoutType));

        List<Student> students = studentService.getStudentsBySectionId(id);

        // Empty seat grid (no assignments yet)
        Map<String, SeatGridData.SeatData> seatAssignments = new HashMap<>();
        List<SeatGridData.SeatInfo> allSeats = new ArrayList<>();
        List<String> disabledSeats = layout.getDisabledSeats() != null ? layout.getDisabledSeats() : new ArrayList<>();

        for (int r = 1; r <= layout.getRows(); r++) {
            for (int c = 1; c <= layout.getColumns(); c++) {
                String seatId = r + "-" + c;
                SeatGridData.SeatInfo seatInfo = new SeatGridData.SeatInfo();
                seatInfo.setId(seatId);
                seatInfo.setRow(r);
                seatInfo.setCol(c);

                if (disabledSeats.contains(seatId)) {
                    seatInfo.setStatus("disabled");
                } else {
                    seatInfo.setStatus("available");
                }

                allSeats.add(seatInfo);
            }
        }

        SeatGridData gridData = new SeatGridData(
                id,
                layout.getId(),
                layout.getPresetId() != null ? layout.getPresetId() : layout.getLayoutType().name(),
                layout.getRows(),
                layout.getColumns(),
                disabledSeats,
                students,
                seatAssignments,
                allSeats
        );

        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("layoutType", layoutType);
        model.addAttribute("layoutId", layout.getId());
        model.addAttribute("layout", layout);
        model.addAttribute("students", students);
        model.addAttribute("gridData", gridData);
        
        return "class-mapping-grid";
    }

    @GetMapping("/{id}/class-mappings/{mappingId}")
    public String viewClassMapping(@PathVariable("id") Long id,
                                   @PathVariable("mappingId") Long mappingId,
                                   Model model) {
        AcademicStructure section = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section Id:" + id));
        
        var mappingOpt = classMappingService.getMappingByIdAndSectionId(mappingId, id);
        if (mappingOpt.isEmpty()) {
            throw new IllegalArgumentException("ClassMapping not found: " + mappingId);
        }
        
        ClassroomLayout layout = classroomLayoutService.getAllLayouts().stream()
                .filter(l -> l.getPresetId() != null && l.getPresetId().equals(mappingOpt.get().getLayoutId()))
                .findFirst()
                .orElse(null);

        List<Student> students = studentService.getStudentsBySectionId(id);
        Map<String, String> assignments = mappingOpt.get().getAssignments();

        Map<String, SeatGridData.SeatData> seatAssignments = new HashMap<>();
        List<SeatGridData.SeatInfo> allSeats = new ArrayList<>();
        List<String> disabledSeats = layout != null && layout.getDisabledSeats() != null ? layout.getDisabledSeats() : new ArrayList<>();

        int rows = layout != null ? layout.getRows() : 10;
        int cols = layout != null ? layout.getColumns() : 10;

        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                String seatId = r + "-" + c;
                SeatGridData.SeatInfo seatInfo = new SeatGridData.SeatInfo();
                seatInfo.setId(seatId);
                seatInfo.setRow(r);
                seatInfo.setCol(c);

                if (disabledSeats.contains(seatId)) {
                    seatInfo.setStatus("disabled");
                } else if (assignments.containsKey(seatId)) {
                    String studentId = assignments.get(seatId);
                    Student student = students.stream()
                            .filter(s -> s.getStudentId().equals(studentId))
                            .findFirst()
                            .orElse(null);
                    seatInfo.setStatus("occupied");
                    seatInfo.setStudentId(studentId);
                    if (student != null) {
                        seatInfo.setStudentName(student.getFirstName() + " " + student.getLastName());
                        seatInfo.setInitials(getInitials(seatInfo.getStudentName()));
                    }
                } else {
                    seatInfo.setStatus("available");
                }

                allSeats.add(seatInfo);
            }
        }

        SeatGridData gridData = new SeatGridData(
                id,
                layout != null ? layout.getId() : null,
                mappingOpt.get().getLayoutId(),
                rows,
                cols,
                disabledSeats,
                students,
                seatAssignments,
                allSeats
        );

        model.addAttribute("section", section);
        model.addAttribute("sectionId", id);
        model.addAttribute("mapping", mappingOpt.get());
        model.addAttribute("layoutType", mappingOpt.get().getLayoutId());
        model.addAttribute("layoutId", layout != null ? layout.getId() : null);
        model.addAttribute("layout", layout);
        model.addAttribute("students", students);
        model.addAttribute("gridData", gridData);
        
        return "class-mapping-view";
    }
}
