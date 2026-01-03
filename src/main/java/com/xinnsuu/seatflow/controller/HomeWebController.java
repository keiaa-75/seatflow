package com.xinnsuu.seatflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeWebController {

    @GetMapping("/")
    public String home(@RequestParam(required = false, defaultValue = "false") Boolean fragment) {
        if (fragment) {
            return "fragments/home-content :: content";
        }
        return "home";
    }

    @GetMapping("/about")
    public String about(@RequestParam(required = false, defaultValue = "false") Boolean fragment) {
        if (fragment) {
            return "fragments/about-content :: content";
        }
        return "about";
    }

    @GetMapping("/manage")
    public String manage() {
        return "redirect:/sections";
    }
}
