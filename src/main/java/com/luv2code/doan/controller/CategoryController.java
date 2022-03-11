package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;




    @GetMapping("/admin/category")
    public String ViewCategory(Model model)
    {
        Page<Category> page = categoryService.listByPage(1);
        List<Category> listCategory = page.getContent();
        long startCount = (1 - 1) * categoryService.CATEGORY_PER_PAGE + 1;
        long endCount = startCount +  categoryService.CATEGORY_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }



        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", 1);

        model.addAttribute("listCategory", listCategory);


        return "category/categorys";
    }
}
