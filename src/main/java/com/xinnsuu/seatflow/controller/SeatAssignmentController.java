package com.xinnsuu.seatflow.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinnsuu.seatflow.model.AssignmentGenerateRequest;
import com.xinnsuu.seatflow.model.SeatAssignment;
import com.xinnsuu.seatflow.model.SeatAssignmentDetailDTO;
import com.xinnsuu.seatflow.service.AssignmentPresetService;
import com.xinnsuu.seatflow.service.SeatAssignmentService;

@RestController
@RequestMapping("/api/sections/{sectionId}/assignments")
public class SeatAssignmentController {

	@Autowired
	private SeatAssignmentService seatAssignmentService;

	@Autowired
	private AssignmentPresetService assignmentPresetService;

	@GetMapping
    public ResponseEntity<List<SeatAssignment>> getAllAssignments(@PathVariable Long sectionId) {
        List<SeatAssignment> assignments = seatAssignmentService.getAssignmentsBySectionId(sectionId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<List<SeatAssignmentDetailDTO>> getAssignmentDetails(@PathVariable Long sectionId) {
        List<SeatAssignmentDetailDTO> details = seatAssignmentService.getAssignmentDetailsBySectionId(sectionId);
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatAssignment> getAssignmentById(@PathVariable Long sectionId, @PathVariable Long id) {
        Optional<SeatAssignment> assignment = seatAssignmentService.getAssignmentByIdAndSectionId(id, sectionId);

        return assignment.map(a -> new ResponseEntity<>(a, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<SeatAssignment> createAssignment(
            @PathVariable Long sectionId,
            @Valid @RequestBody SeatAssignment assignment) {

        try {
            SeatAssignment savedAssignment = seatAssignmentService.createAssignmentForSection(sectionId, assignment);
            return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<List<SeatAssignment>> generateAssignments(
            @PathVariable Long sectionId,
            @Valid @RequestBody AssignmentGenerateRequest request) {

        try {
            List<SeatAssignment> assignments = assignmentPresetService.generateAssignments(
                    sectionId,
                    request.getLayoutId(),
                    request.getPresetType(),
                    request.getAssignmentName()
            );
            return new ResponseEntity<>(assignments, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatAssignment> updateAssignment(
            @PathVariable Long sectionId,
            @PathVariable Long id, 
            @Valid @RequestBody SeatAssignment updatedAssignment) {

        try {
            SeatAssignment updated = seatAssignmentService.updateAssignmentForSection(sectionId, id, updatedAssignment);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long sectionId, @PathVariable Long id) {
        
        try {
            seatAssignmentService.deleteAssignmentForSection(sectionId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}