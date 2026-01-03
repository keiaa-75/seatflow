package com.xinnsuu.seatflow.controller;

import java.util.List;

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

import com.xinnsuu.seatflow.model.AcademicStructure;
import com.xinnsuu.seatflow.service.AcademicStructureService;

@Controller
@RequestMapping("/sections")
public class AcademicStructureWebController {

    @Autowired
    private AcademicStructureService academicStructureService;

    @GetMapping
    public String listStructures(@RequestParam(required = false) String grade,
            @RequestParam(required = false, defaultValue = "false") Boolean fragment, Model model) {
        List<AcademicStructure> structures;
        if ("ELEVEN".equals(grade)) {
            structures = academicStructureService.getSectionsByGradeLevel(
                com.xinnsuu.seatflow.model.enums.GradeLevel.ELEVEN);
        } else if ("TWELVE".equals(grade)) {
            structures = academicStructureService.getSectionsByGradeLevel(
                com.xinnsuu.seatflow.model.enums.GradeLevel.TWELVE);
        } else {
            structures = academicStructureService.getAllSections();
        }
        model.addAttribute("structures", structures);
        if (fragment) {
            return "fragments/pages/sections-content :: content";
        }
        return "sections";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<AcademicStructure>> searchApi(@RequestParam("q") String query) {
        List<AcademicStructure> results = academicStructureService.searchSections(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false, defaultValue = "false") Boolean fragment, Model model) {
        model.addAttribute("structure", new AcademicStructure());
        if (fragment) {
            return "fragments/forms/section-form :: form";
        }
        return "sections-edit";
    }

    @PostMapping("/new")
    public String createStructure(@Valid @ModelAttribute("structure") AcademicStructure structure, BindingResult result,
            @RequestParam(required = false, defaultValue = "false") Boolean fragment, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("structure", structure);
            if (fragment) {
                return "fragments/forms/section-form :: form";
            }
            return "sections-edit";
        }
        academicStructureService.createAcademicStructure(structure);
        return "redirect:/sections";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, @RequestParam(required = false, defaultValue = "false") Boolean fragment, Model model) {
        AcademicStructure structure = academicStructureService.getSectionById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid structure Id:" + id));
        model.addAttribute("structure", structure);
        if (fragment) {
            return "fragments/forms/section-form :: form";
        }
        return "sections-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStructure(@PathVariable("id") Long id, @Valid @ModelAttribute("structure") AcademicStructure structure,
            BindingResult result, @RequestParam(required = false, defaultValue = "false") Boolean fragment, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("structure", structure);
            if (fragment) {
                return "fragments/forms/section-form :: form";
            }
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
}
