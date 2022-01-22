package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Review;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.ProductService;
import com.luv2code.doan.service.ReviewService;
import com.luv2code.doan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Date;

@Controller
public class ReviewController {
    private final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/product/review")
    public String addReview(@AuthenticationPrincipal UserPrincipal loggedUser, @Valid Review review, BindingResult errors,
                            RedirectAttributes redirectAttributes, @RequestParam(name = "productId") Integer productId) {
        Integer id = loggedUser.getId();
        try {
            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("messageError", "Vote hoac bình luận không được bỏ trống!");
                return "redirect:/product/detail/" + productId;
            } else{
                Product product = productService.getProductById(productId);
                User user = userService.getUserByID(id);
                review.setUser(user);
                review.setProduct(product);
                review.setDate(new Date());

                reviewService.saveReview(review);

                log.info(review.toString());
                redirectAttributes.addFlashAttribute("messageSuccess", "The comment has been added successfully.");
                return "redirect:/product/detail/" + productId;
            }

        }
        catch (UserNotFoundException | ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/product/detail/" + productId;
        }
    }

}
