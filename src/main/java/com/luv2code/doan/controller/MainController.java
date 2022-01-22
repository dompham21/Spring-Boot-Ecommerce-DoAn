package com.luv2code.doan.controller;



import com.luv2code.doan.entity.Product;
import com.luv2code.doan.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        return "admin";
    }

    @GetMapping("/")
    public String index(Model model) {
        Page<Product> page = productService.listLatestProduct();
        List<Product> listLatestProducts = page.getContent();

        model.addAttribute("listLatestProducts", listLatestProducts);

        return "index";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }




}
