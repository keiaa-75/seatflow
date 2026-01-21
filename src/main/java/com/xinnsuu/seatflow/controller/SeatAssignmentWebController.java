package com.xinnsuu.seatflow.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinnsuu.seatflow.dto.SeatGridData;
import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.model.SeatAssignmentDetailDTO;
import com.xinnsuu.seatflow.model.Student;
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

    @Autowired
    private ObjectMapper objectMapper;

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
        ClassroomLayout layout = classroomLayoutService.getLayoutById(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid layout Id:" + layoutId));

        List<Student> students = studentService.getStudentsBySectionId(sectionId);
        List<SeatAssignmentDetailDTO> existingAssignments = 
                seatAssignmentService.getAssignmentDetailsBySectionId(sectionId);

        Map<String, SeatGridData.SeatData> seatAssignments = new HashMap<>();
        for (SeatAssignmentDetailDTO assignment : existingAssignments) {
            if (assignment.getLayoutId().equals(layoutId)) {
                String seatKey = (assignment.getRowNumber() - 1) + "-" + (assignment.getColumnNumber() - 1);
                seatAssignments.put(seatKey, new SeatGridData.SeatData(
                        assignment.getStudentId(),
                        assignment.getStudentName(),
                        getInitials(assignment.getStudentName())
                ));
            }
        }

        List<SeatGridData.SeatInfo> allSeats = new ArrayList<>();
        List<String> disabledSeats = layout.getDisabledSeats() != null ? layout.getDisabledSeats() : new ArrayList<>();

        for (int r = 0; r < layout.getRows(); r++) {
            for (int c = 0; c < layout.getColumns(); c++) {
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
                sectionId,
                layout.getId(),
                layout.getPresetId() != null ? layout.getPresetId() : layout.getLayoutType().name(),
                layout.getRows(),
                layout.getColumns(),
                disabledSeats,
                students,
                seatAssignments,
                allSeats
        );

        model.addAttribute("assignment", new SeatAssignment());
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("layoutId", layoutId);
        model.addAttribute("layout", layout);
        model.addAttribute("students", students);
        model.addAttribute("gridData", gridData);

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

    @GetMapping("/new/assign/data")
    @ResponseBody
    public SeatGridData getSeatGridData(@RequestParam("sectionId") Long sectionId,
                                        @RequestParam("layoutId") Long layoutId) {
        ClassroomLayout layout = classroomLayoutService.getLayoutById(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid layout Id:" + layoutId));

        List<Student> students = studentService.getStudentsBySectionId(sectionId);

        List<SeatAssignmentDetailDTO> existingAssignments = 
                seatAssignmentService.getAssignmentDetailsBySectionId(sectionId);

        Map<String, SeatGridData.SeatData> seatAssignments = new HashMap<>();
        for (SeatAssignmentDetailDTO assignment : existingAssignments) {
            if (assignment.getLayoutId().equals(layoutId)) {
                String seatKey = (assignment.getRowNumber() - 1) + "-" + (assignment.getColumnNumber() - 1);
                seatAssignments.put(seatKey, new SeatGridData.SeatData(
                        assignment.getStudentId(),
                        assignment.getStudentName(),
                        getInitials(assignment.getStudentName())
                ));
            }
        }

        List<SeatGridData.SeatInfo> allSeats = new ArrayList<>();
        List<String> disabledSeats = layout.getDisabledSeats() != null ? layout.getDisabledSeats() : new ArrayList<>();

        for (int r = 0; r < layout.getRows(); r++) {
            for (int c = 0; c < layout.getColumns(); c++) {
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

        return new SeatGridData(
                sectionId,
                layout.getId(),
                layout.getPresetId() != null ? layout.getPresetId() : layout.getLayoutType().name(),
                layout.getRows(),
                layout.getColumns(),
                disabledSeats,
                students,
                seatAssignments,
                allSeats
        );
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

    @GetMapping("/students/json")
    @ResponseBody
    public String getStudentsJson(@RequestParam("sectionId") Long sectionId) throws JsonProcessingException {
        List<Student> students = studentService.getStudentsBySectionId(sectionId);
        return objectMapper.writeValueAsString(students);
    }
}
