package com.luv2code.doan.controller;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("admin/brand")
    public String viewBrand(Model model) {
        List<Brand> listBrand = brandService.getAllBrand();

        model.addAttribute("listBrand", listBrand);
        return "brand/brands";
    }
}
