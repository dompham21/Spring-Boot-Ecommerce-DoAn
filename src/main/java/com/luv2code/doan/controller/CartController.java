package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.ProductService;
import com.luv2code.doan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CartController {
    private final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @PostMapping("/cart/add")
    public String addProductToCart(@RequestParam("productId") Integer productId,
                                                   @RequestParam("quantity") Integer quantity,
                                                   @AuthenticationPrincipal UserPrincipal loggedUser,

                                                    RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        log.info(referer);
        try {
            if(loggedUser == null) {
                return "redirect:/login";
            }


            User user = userService.getUserByID(loggedUser.getId());
            Product product = productService.getProductById(productId);
            Integer updatedQuantity = cartService.addProductToCart(product, user, quantity);
            redirectAttributes.addFlashAttribute("messageSuccess",  updatedQuantity + " item(s) of this product were added to your shopping cart.");
            return "redirect:" + referer;
        }
        catch (UserNotFoundException e) {
            return "redirect:/login";
        }
        catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError",  "Can't add this product. Product not found.");
            return "redirect:" + referer;
        }
    }


}
