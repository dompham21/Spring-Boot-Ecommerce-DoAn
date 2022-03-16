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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable("productId") Integer productId,
                                              @PathVariable("quantity") Integer quantity,
                                              RedirectAttributes redirectAttributes,
                                              @AuthenticationPrincipal UserPrincipal loggedUser,
                                              HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        log.info(referer);
        try {
            if(loggedUser == null) {
                return "redirect:/login";
            }
            User user = userService.getUserByID(loggedUser.getId());
            Product product = productService.getProductById(productId);

            if(quantity > product.getInStock()) {
                redirectAttributes.addFlashAttribute("messageError", "Can't change quantity this product. Quantity can't more than " + product.getInStock() );
                return "redirect:" + referer;
            }
            else if(quantity <= 0 ) {
                redirectAttributes.addFlashAttribute("messageError", "Can't change quantity this product. Quantity can't less than or equal to 0" );
                return "redirect:" + referer;
            }
            cartService.updatedQuantity(product, user, quantity);
            redirectAttributes.addFlashAttribute("messageSuccess",  "Item(s) of this product were updated to your shopping cart");

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

    @GetMapping("/cart/delete/{productId}")
    public String updateQuantity(@PathVariable("productId") Integer productId,
                                              @AuthenticationPrincipal UserPrincipal loggedUser,
                                              HttpServletRequest request,RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        log.info(referer);

        try {
            if (loggedUser == null) {
                return "redirect:/login";
            }
            User user = userService.getUserByID(loggedUser.getId());
            Product product = productService.getProductById(productId);

            cartService.deleteCartItem(user.getId(), product.getId());
            redirectAttributes.addFlashAttribute("messageSuccess",  "This product were delete from your shopping caart");

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
