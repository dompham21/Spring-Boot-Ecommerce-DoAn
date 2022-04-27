package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Review;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.ReviewNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.CartService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class ReviewController {
    private final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

    @PostMapping("/product/review")
    public String addReview(@AuthenticationPrincipal UserPrincipal loggedUser, Review review, BindingResult errors,
                            RedirectAttributes redirectAttributes, @RequestParam(name = "productId") Integer productId, Model model) {
        try {
            Integer id = loggedUser.getId();

            if(review.getComment().trim().length() == 0) {
                errors.rejectValue("comment", "review", "Bình luận không được bỏ trống!");
            }
            if(review.getComment().trim().length() > 300) {
                errors.rejectValue("comment", "review", "Bình luận không được lon hon 300 ky tu!");
            }
            if(review.getVote() == null) {
                errors.rejectValue("vote", "review", "Vote không được bo trong!");
            }
            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.review", errors);
                redirectAttributes.addFlashAttribute("review", review);
                redirectAttributes.addFlashAttribute("isReview", true);
                return "redirect:/product/detail/" + productId;
            } else{
                Product product = productService.getProductById(productId);
                User user = userService.getUserByID(id);
                review.setUser(user);
                review.setProduct(product);
                review.setDate(new Date());

                reviewService.saveReview(review);

                log.info(review.toString());
                redirectAttributes.addFlashAttribute("isReview", true);
                redirectAttributes.addFlashAttribute("messageSuccess", "Đánh giá đã được thêm thành công.");
                return "redirect:/product/detail/" + productId;
            }

        }
        catch (UserNotFoundException | ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/product/detail/" + productId;
        }
    }

    @GetMapping("/product/review/delete/{productId}/{id}")
    public String deleteReview(@PathVariable(name = "id") Integer id, @PathVariable(name = "productId") Integer productId, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("isReview", true);
            redirectAttributes.addFlashAttribute("messageSuccess", "Đánh giá ID " + id + " đã được xóa thành công");
        }
        catch (ReviewNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/product/detail/" + productId;
    }

    @GetMapping("/product/review/edit/{productId}/{id}")
    public String editReview(@PathVariable(name = "id") Integer id, @PathVariable(name = "productId") Integer productId,
                             RedirectAttributes redirectAttributes, Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        Integer userId = loggedUser.getId();

        try {
            User user = userService.getUserByID(userId);
            Product product = productService.getProductById(productId);
            List<Cart> listCarts = cartService.findCartByUser(loggedUser.getId());

            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();
            }

            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);
            model.addAttribute("user", user);
            model.addAttribute("product", product);

            if (!model.containsAttribute("review")) {
                Review review = reviewService.getReviewById(id);
                model.addAttribute("review", review);
            }

            return "profile-user/edit-review";
        }
        catch (ReviewNotFoundException | UserNotFoundException | ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/product/detail/" + productId;
        }
    }
    @PostMapping("/product/review/edit/{productId}/{id}")
    public String saveEditReview(Review review, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable(name = "id") Integer id,
                                  @PathVariable(name = "productId") Integer productId) {
        try {
            Review existReview = reviewService.getReviewById(id);

            if(review.getComment().trim().length() == 0) {
                errors.rejectValue("comment", "review", "Bình luận không được bỏ trống!");
            }
            if(review.getComment().trim().length() > 300) {
                errors.rejectValue("comment", "review", "Bình luận không được lon hon 300 ky tu!");
            }
            if(review.getVote() == null) {
                errors.rejectValue("vote", "review", "Vote không được bo trong!");
            }
            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.review", errors);
                redirectAttributes.addFlashAttribute("review", review);
                redirectAttributes.addFlashAttribute("isReview", true);
                return "redirect:/product/review/edit/" + productId + "/" + id;
            } else {
                review.setDate(existReview.getDate());
                review.setUser(existReview.getUser());
                review.setProduct(existReview.getProduct());
                reviewService.saveReview(review);
                redirectAttributes.addFlashAttribute("isReview", true);
                redirectAttributes.addFlashAttribute("messageSuccess", "Đánh giá đã được chỉnh sửa thành công.");
                return "redirect:/product/detail/" + productId;
            }
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute("isReview", true);
            redirectAttributes.addFlashAttribute("messageError", e);
            return "redirect:/product/detail/" + productId;
        }
    }
}
