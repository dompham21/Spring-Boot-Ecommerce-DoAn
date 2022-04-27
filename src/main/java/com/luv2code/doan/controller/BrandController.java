package com.luv2code.doan.controller;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.service.BrandService;
import com.luv2code.doan.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class BrandController {
    private final Logger log = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private BrandService brandService;

    @GetMapping("/admin/brand")
    public String listFirstPage() {
        return "redirect:/admin/brand/page/1";
    }

    @GetMapping("/admin/brand/page/{pageNum}")
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


        Page<Brand> page = brandService.listByPage(pageNum, keyword, sortField, sortDir);
        List<Brand> listBrands = page.getContent();
        long startCount = (pageNum - 1) * brandService.BRAND_PER_PAGE + 1;
        long endCount = startCount +  brandService.BRAND_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }



        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);

        model.addAttribute("listBrands", listBrands);


        return "brand/brands";
    }

    @GetMapping("admin/brand/add")
    public String addBrand(Model model) {
        Brand brand = new Brand();
        model.addAttribute("brand", brand);

        return "brand/new_brand";
    }

    @PostMapping("admin/brand/add")
    public String saveBrand(Brand brand, BindingResult errors, RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file) throws IOException {

        if(brandService.getBrandByName(brand.getName()) != null) {
            errors.rejectValue("name", "brand", "Tên thương hiệu này đã có!");
        }

        if(brand.getName().trim().length() == 0) {
            errors.rejectValue("name", "brand", "Vui lòng nhập tên thương hiệu!");
        }
        else if(brand.getName().length() > 100) {
            errors.rejectValue("name", "brand", "Tên thương hiệu không được dài quá 100 ký tự!");
        }

        if(brand.getDescription().trim().length() == 0) {
            errors.rejectValue("description", "brand", "Vui lòng nhập mô tả thương hiệu!");
        }
        else if(brand.getDescription().length() > 200) {
            errors.rejectValue("description", "brand", "Mô tả không được dài quá 200 ký tự!");
        }

        if(brand.getLogo().trim().length() == 0) {
            errors.rejectValue("image", "brand", "Vui lòng nhập hinh anh thương hiệu!");
        }


        if(errors.hasErrors()) {
            return "brand/new_brand";
        }
        else {
            String url = storageService.upload(file);

            brand.setLogo(url);
            brandService.saveBrand(brand);
            redirectAttributes.addFlashAttribute("messageSuccess", "The brand has been saved successfully.");
            return "redirect:/admin/brand";

        }
    }

    @GetMapping("/admin/brand/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Brand brand = brandService.getBrandById(id);

            model.addAttribute("brand", brand);
            return "brand/new_brand";
        }
        catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/brand";

        }
    }

    @PostMapping("/admin/brand/edit/{id}")
    public String saveEditBrand(Brand brand, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {

        try {
            Brand existBrand = brandService.getBrandById(id);

            Brand brandCheckUnique = brandService.getBrandByName(brand.getName());

            if(brand.getName().trim().length() == 0) {
                errors.rejectValue("name", "brand", "Vui lòng nhập tên thương hiệu!");
            }
            else if(brand.getName().length() > 100) {
                errors.rejectValue("name", "brand", "Tên thương hiệu không được dài quá 100 ký tự!");
            }

            if(brandCheckUnique != null && !brandCheckUnique.getId().equals(existBrand.getId())) {
                errors.rejectValue("name", "brand", "Tên thương hiệu này đã có!");
            }


            if(brand.getDescription().trim().length() == 0) {
                errors.rejectValue("description", "brand", "Vui lòng nhập mô tả thương hiệu!");
            }
            else if(brand.getDescription().length() > 200) {
                errors.rejectValue("description", "brand", "Mô tả không được dài quá 200 ký tự!");
            }

            if(brand.getLogo().trim().length() == 0) {
                errors.rejectValue("image", "brand", "Vui lòng nhập hinh anh thương hiệu!");
            }
            if (errors.hasErrors()) {
                return "brand/new_brand";
            } else {
                if(!existBrand.getLogo().equals(brand.getLogo())) {
                    String url = storageService.upload(file);
                    brand.setLogo(url);
                }


                brandService.saveBrand(brand);

                redirectAttributes.addFlashAttribute("messageSuccess", "The brand has been edited successfully.");
                return "redirect:/admin/brand";


            }
        } catch (BrandNotFoundException | IOException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/brand";
        }
    }

    @GetMapping("/admin/brand/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            brandService.deleteBrand(id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The brand ID " + id + " has been deleted successfully");
        }
        catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/admin/brand/page/1";
    }

}
