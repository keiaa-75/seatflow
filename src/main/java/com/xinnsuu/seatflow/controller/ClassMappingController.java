package com.xinnsuu.seatflow.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.xinnsuu.seatflow.model.ClassMapping;
import com.xinnsuu.seatflow.service.ClassMappingService;

@RestController
@RequestMapping("/api/sections/{sectionId}/class-mappings")
public class ClassMappingController {

    @Autowired
    private ClassMappingService classMappingService;

    @GetMapping
    public ResponseEntity<List<ClassMapping>> getAllMappings(@PathVariable Long sectionId) {
        List<ClassMapping> mappings = classMappingService.getMappingsBySectionId(sectionId);
        return new ResponseEntity<>(mappings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassMapping> getMappingById(@PathVariable Long sectionId, @PathVariable Long id) {
        Optional<ClassMapping> mapping = classMappingService.getMappingByIdAndSectionId(id, sectionId);
        return mapping.map(m -> new ResponseEntity<>(m, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ClassMapping> createMapping(
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String layoutId = (String) request.get("layoutId");
            @SuppressWarnings("unchecked")
            Map<String, String> assignments = (Map<String, String>) request.get("assignments");
            
            ClassMapping mapping = classMappingService.createMapping(sectionId, name, layoutId, assignments);
            return new ResponseEntity<>(mapping, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassMapping> updateMapping(
            @PathVariable Long sectionId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String layoutId = (String) request.get("layoutId");
            @SuppressWarnings("unchecked")
            Map<String, String> assignments = (Map<String, String>) request.get("assignments");
            
            ClassMapping mapping = classMappingService.updateMapping(sectionId, id, name, layoutId, assignments);
            return new ResponseEntity<>(mapping, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long sectionId, @PathVariable Long id) {
        try {
            classMappingService.deleteMapping(sectionId, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/auto-save")
    public ResponseEntity<Map<String, Object>> autoSaveAssignment(
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> request) {
        try {
            String seatId = (String) request.get("seatId");
            String studentId = (String) request.get("studentId");
            Long mappingId = request.get("mappingId") != null ? Long.valueOf(request.get("mappingId").toString()) : null;
            
            if (seatId == null || studentId == null || mappingId == null) {
                return new ResponseEntity<>(Map.of("error", "Missing required fields: seatId=" + seatId + ", studentId=" + studentId + ", mappingId=" + mappingId), HttpStatus.BAD_REQUEST);
            }
            
            // Get the specific ClassMapping by ID
            Optional<ClassMapping> mappingOpt = classMappingService.getMappingByIdAndSectionId(mappingId, sectionId);
            if (mappingOpt.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "ClassMapping not found: " + mappingId), HttpStatus.NOT_FOUND);
            }
            
            ClassMapping mapping = mappingOpt.get();
            Map<String, String> assignments = mapping.getAssignments();
            if (assignments == null) {
                assignments = new java.util.HashMap<>();
            }
            assignments.put(seatId, studentId);
            
            classMappingService.updateMapping(sectionId, mappingId, mapping.getName(), mapping.getLayoutId(), assignments);
            
            return new ResponseEntity<>(Map.of(
                "mappingId", mappingId,
                "action", "updated",
                "seatId", seatId,
                "studentId", studentId
            ), HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initMapping(
            @PathVariable Long sectionId,
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String layoutId = (String) request.get("layoutId");
            
            if (name == null || name.trim().isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Name is required"), HttpStatus.BAD_REQUEST);
            }
            if (layoutId == null) {
                return new ResponseEntity<>(Map.of("error", "Layout ID is required"), HttpStatus.BAD_REQUEST);
            }
            
            ClassMapping mapping = classMappingService.createMapping(sectionId, name.trim(), layoutId, new java.util.HashMap<>());
            
            return new ResponseEntity<>(Map.of(
                "mappingId", mapping.getId(),
                "name", mapping.getName(),
                "layoutId", mapping.getLayoutId()
            ), HttpStatus.CREATED);
            
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
