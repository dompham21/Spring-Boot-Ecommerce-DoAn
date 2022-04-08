package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Poster;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.PosterNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.service.PosterService;
import com.luv2code.doan.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PosterController {
    @Autowired
    private PosterService posterService;

    @Autowired
    private StorageService storageService;

    @GetMapping("/admin/poster")
    public String listPoster(Model model) {
        List<Poster> listPostersLeft = posterService.listPosterLeft();
        List<Poster> listPostersRight = posterService.listPosterRight();
        model.addAttribute("listPostersLeft",listPostersLeft);
        model.addAttribute("listPostersRight",listPostersRight);


        return "poster/posters";
    }

    @GetMapping("/admin/poster/add")
    public String addPoster(Model model) {
        Poster poster = new Poster();
        poster.setIsActive(true);
        model.addAttribute("poster", poster);
        return "poster/new_poster_left";
    }

    @PostMapping("/admin/poster/add")
    public String savePoster(Poster poster, BindingResult errors, RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file) throws StorageUploadFileException, IOException {

        if(poster.getTitle().trim().length() == 0) {
            errors.rejectValue("title", "poster", "Vui lòng nhập title áp phích!");
        }

        if(poster.getSubTitle().trim().length() == 0) {
            errors.rejectValue("subTitle", "poster", "Vui lòng nhập sub title áp phích!");
        }
        if(poster.getDescription().trim().length() == 0) {
            errors.rejectValue("description", "poster", "Description không được bỏ trống!");
        }
        if(poster.getImage() == null) {
            errors.rejectValue("image", "poster", "Image không được bỏ trống!");
        }
        if(errors.hasErrors()) {
            return "product/new_product_left";
        }

        else {
            String url = storageService.upload(file);
            poster.setImage(url);
            poster.setType("left");
            posterService.savePoster(poster);
            redirectAttributes.addFlashAttribute("messageSuccess", "The poster has been saved successfully.");
            return "redirect:/admin/poster";
        }

    }


    @GetMapping("/admin/poster/left/edit/{id}")
    public String editPosterLeft(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Poster poster = posterService.getPosterByIdAndType(id, "left");
            model.addAttribute("poster", poster);
            return "poster/new_poster_left";
        }
        catch (PosterNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/poster";

        }
    }

    @GetMapping("/admin/poster/right/edit/{id}")
    public String editPosterRight(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            Poster poster = posterService.getPosterByIdAndType(id, "right");
            model.addAttribute("poster", poster);
            return "poster/new_poster_right";
        }
        catch (PosterNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/poster";

        }
    }

    @PostMapping("/admin/poster/left/edit/{id}")
    public String saveEditPosterLeft(Poster poster, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {

        try {
            Poster posterExists = posterService.getPosterByIdAndType(id, "left");

            if(poster.getTitle().trim().length() == 0) {
                errors.rejectValue("title", "poster", "Vui lòng nhập title áp phích!");
            }

            if(poster.getSubTitle().trim().length() == 0) {
                errors.rejectValue("subTitle", "poster", "Vui lòng nhập sub title áp phích!");
            }
            if(poster.getDescription().trim().length() == 0) {
                errors.rejectValue("description", "poster", "Description không được bỏ trống!");
            }
            if(poster.getImage() == null) {
                errors.rejectValue("image", "poster", "Image không được bỏ trống!");
            }
            if (errors.hasErrors()) {
                return "poster/new_poster";
            } else {
                if(!posterExists.getImage().equals(poster.getImage())) {
                    String url = storageService.upload(file);
                    poster.setImage(url);
                }

                poster.setType(posterExists.getType());

                posterService.savePoster(poster);

                redirectAttributes.addFlashAttribute("messageSuccess", "The poster has been edited successfully.");
                return "redirect:/admin/poster";


            }
        } catch ( IOException | PosterNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/poster";
        }
    }

    @PostMapping("/admin/poster/right/edit/{id}")
    public String saveEditPosterRight(Poster poster, BindingResult errors, RedirectAttributes redirectAttributes,
                                     @PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {

        try {
            Poster posterExists = posterService.getPosterByIdAndType(id, "right");
            if(poster.getImage() == null) {
                errors.rejectValue("image", "poster", "Image không được bỏ trống!");
            }
            if (errors.hasErrors()) {
                return "poster/new_poster";
            } else {
                if(!posterExists.getImage().equals(poster.getImage())) {
                    String url = storageService.upload(file);
                    poster.setImage(url);
                }

                poster.setIsActive(true);
                poster.setType(posterExists.getType());

                posterService.savePoster(poster);

                redirectAttributes.addFlashAttribute("messageSuccess", "The poster has been edited successfully.");
                return "redirect:/admin/poster";


            }
        } catch ( IOException | PosterNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/poster";
        }
    }


    @GetMapping("/admin/poster/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            posterService.deletePoster(id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The poster ID " + id + " has been deleted successfully");
        }
        catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/admin/poster";
    }
}
