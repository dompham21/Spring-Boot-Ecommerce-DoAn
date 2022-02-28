package com.luv2code.doan.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryController {
    @GetMapping("/admin/category")
    public String ViewCategory()
    {
        return "product/new_product";
    }
}
