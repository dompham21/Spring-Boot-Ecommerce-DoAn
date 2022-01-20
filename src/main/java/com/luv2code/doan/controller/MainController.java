package com.luv2code.doan.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(MainController.class);


    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        return "admin";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }




}
