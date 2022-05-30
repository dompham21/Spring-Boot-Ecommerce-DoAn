package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.service.CategoryService;
import com.luv2code.doan.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService;



    @GetMapping("/admin/category")
    public String listFirstPage() {
        return "redirect:/admin/category/page/1";
    }

    @GetMapping("/admin/category/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
    @RequestParam(defaultValue = "") String keyword,
    @RequestParam(defaultValue = "id") String sortField,
    @RequestParam(required = false) String sortDir) {

        model.addAttribute("sortField", sortField);

        if(sortDir == null) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);


        Page<Category> page = categoryService.listByPage(pageNum, keyword, sortField, sortDir);
        List<Category> listCategories = page.getContent();
        long startCount = (pageNum - 1) * categoryService.CATEGORY_PER_PAGE + 1;
        long endCount = startCount +  categoryService.CATEGORY_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }



        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);

        model.addAttribute("listCategories", listCategories);


        return "category/categories";
    }

    @GetMapping("admin/category/add")
    public String addCategory(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);

        return "category/new_category";
    }
    @PostMapping("admin/category/add")
    public String saveCategory(Category category, BindingResult errors, RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file) throws IOException {

        if(categoryService.getCategoryByName(category.getName()) != null) {
            errors.rejectValue("name", "category", "Tên thể loại này đã có!");
        }

        if(category.getName().trim().length() == 0) {
            errors.rejectValue("name", "category", "Vui lòng nhập tên thể loại!");
        }
        else if(category.getName().length() > 100) {
            errors.rejectValue("name", "category", "Tên thể loại không được dài quá 100 ký tự!");
        }

        if(category.getDescription().trim().length() == 0) {
            errors.rejectValue("description", "category", "Vui lòng nhập mô tả thể loại!");
        }
        else if(category.getDescription().length() > 200) {
            errors.rejectValue("description", "category", "Mô tả không được dài quá 200 ký tự!");
        }

        if(category.getImage().trim().length() == 0) {
            errors.rejectValue("image", "category", "Vui lòng nhập hinh anh thể loại!");
        }


        if(errors.hasErrors()) {
            return "category/new_category";
        }
        else {
            String url = storageService.upload(file);

            category.setImage(url);
            categoryService.saveCategory(category);
            redirectAttributes.addFlashAttribute("messageSuccess", "The category has been saved successfully.");
            return "redirect:/admin/category";
        }
    }

    @GetMapping("/admin/category/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Category category = categoryService.getCategoryById(id);

            model.addAttribute("category", category);
            return "category/new_category";
        }
        catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/category";

        }
    }

    @PostMapping("/admin/category/edit/{id}")
    public String saveEditCategory(Category category, BindingResult errors, RedirectAttributes redirectAttributes,
                                @PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {

        try {
            Category existCategory = categoryService.getCategoryById(id);

            Category categoryCheckUnique = categoryService.getCategoryByName(category.getName());

            if(category.getName().trim().length() == 0) {
                errors.rejectValue("name", "category", "Vui lòng nhập tên thể loại!");
            }
            else if(category.getName().length() > 100) {
                errors.rejectValue("name", "category", "Tên thể loại không được dài quá 100 ký tự!");
            }

            if(categoryCheckUnique != null && !categoryCheckUnique.getId().equals(existCategory.getId())) {
                errors.rejectValue("name", "category", "Tên thể loại này đã có!");
            }


            if(category.getDescription().trim().length() == 0) {
                errors.rejectValue("description", "category", "Vui lòng nhập mô tả thể loại!");
            }
            else if(category.getDescription().length() > 200) {
                errors.rejectValue("description", "category", "Mô tả không được dài quá 200 ký tự!");
            }

            if(category.getImage().trim().length() == 0) {
                errors.rejectValue("image", "category", "Vui lòng nhập hinh anh thể loại!");
            }
            if (errors.hasErrors()) {
                return "category/new_category";
            } else {
                if(!existCategory.getImage().equals(category.getImage())) {
                    String url = storageService.upload(file);
                    category.setImage(url);
                }


                categoryService.saveCategory(category);

                redirectAttributes.addFlashAttribute("messageSuccess", "The category has been edited successfully.");
                return "redirect:/admin/category";


            }
        } catch (CategoryNotFoundException | IOException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/category";
        }
    }

    @GetMapping("/admin/category/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The category ID " + id + " has been deleted successfully");
        }
        catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/admin/category/page/1";
    }


}
