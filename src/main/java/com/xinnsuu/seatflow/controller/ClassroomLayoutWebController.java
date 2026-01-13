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
import org.springframework.web.bind.annotation.RequestParam;

import com.xinnsuu.seatflow.model.ClassroomLayout;
import com.xinnsuu.seatflow.service.ClassroomLayoutService;

@Controller
@RequestMapping("/layouts")
public class ClassroomLayoutWebController {

    @Autowired
    private ClassroomLayoutService classroomLayoutService;

    @GetMapping
    public String listLayouts(Model model) {
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "classroom-layouts";
    }

    @GetMapping("/select")
    public String selectLayout(Model model) {
        model.addAttribute("layouts", classroomLayoutService.getAllLayouts());
        return "classroom-layouts";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(name = "sectionId", required = false) Long sectionId, Model model) {
        model.addAttribute("layout", new ClassroomLayout());
        model.addAttribute("sectionId", sectionId);
        return "classroom-layout-form";
    }

    @PostMapping("/new")
    public String createLayout(@Valid @ModelAttribute("layout") ClassroomLayout layout, 
                              @RequestParam(name = "sectionId", required = false) Long sectionId,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sectionId", sectionId);
            return "classroom-layout-form";
        }
        classroomLayoutService.createLayout(layout);
        
        if (sectionId != null) {
            return "redirect:/sections/" + sectionId + "/assignments/new";
        }
        return "redirect:/layouts";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        ClassroomLayout layout = classroomLayoutService.getLayoutById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid layout Id:" + id));
        model.addAttribute("layout", layout);
        return "classroom-layout-form";
    }

    @PostMapping("/edit/{id}")
    public String updateLayout(@PathVariable("id") Long id, @Valid @ModelAttribute("layout") ClassroomLayout layout,
            BindingResult result) {
        if (result.hasErrors()) {
            return "classroom-layout-form";
        }
        classroomLayoutService.updateLayout(id, layout);
        return "redirect:/layouts";
    }

    @GetMapping("/delete/{id}")
    public String deleteLayout(@PathVariable("id") Long id) {
        classroomLayoutService.deleteLayout(id);
        return "redirect:/layouts";
    }
}
