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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class BrandController {
    private final Logger log = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private BrandService brandService;

    @GetMapping("admin/brand")
    public String viewBrand(Model model) {
        List<Brand> listBrand = brandService.getAllBrand();

        model.addAttribute("listBrand", listBrand);
        return "brand/brands";
    }

    @GetMapping("admin/brand/add")
    public String addBrand(Model model) {
        Brand brand = new Brand();
        model.addAttribute("brand", brand);

        return "brand/new_brand";
    }

    @PostMapping("admin/brand/add")
    public String saveBrand(@Valid Brand brand, BindingResult errors, RedirectAttributes redirectAttributes) throws StorageUploadFileException {
        log.info(brand.toString());
        if(brandService.getBrandByName(brand.getName()) != null) {
            errors.rejectValue("name", "brand", "Ten brand khong duoc trung!");
        }

        if(errors.hasErrors()) {
            log.info("has errors");
            return "brand/new_brand";
        }
        else {
            String url = storageService.uploadFile(brand.getLogo());

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
            model.addAttribute("isEdit", true);
            model.addAttribute("brandName", brand.getName());
            return "brand/new_brand";
        }
        catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/brand";

        }
    }

    @PostMapping("/admin/brand/edit/{id}")
    public String saveEditBrand(@Valid Brand brand, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id) {

        try {
            Brand existBrand = brandService.getBrandById(id);

            Brand brandCheckUnique = brandService.getBrandByName(brand.getName());

            if ((!brand.getName().equals(existBrand.getName())) && (brandCheckUnique != null)) {
                errors.rejectValue("name", "brand", "Ten brand khong duoc trung!");
            }
            if (errors.hasErrors()) {
                log.info("has errors");
                return "brand/new_brand";
            } else {
                if(!existBrand.getLogo().equals(brand.getLogo())) {
                    String url = storageService.uploadFile(brand.getLogo());
                    existBrand.setLogo(url);
                }
                else {
                    existBrand.setLogo(brand.getLogo());
                }
                existBrand.setName(brand.getName());
                existBrand.setDescription(brand.getDescription());

                log.info(existBrand.toString());
                brandService.saveBrand(existBrand);

                redirectAttributes.addFlashAttribute("messageSuccess", "The brand has been edited successfully.");
                return "redirect:/admin/brand";


            }
        } catch (BrandNotFoundException | StorageUploadFileException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/brand";
        }
    }

}
