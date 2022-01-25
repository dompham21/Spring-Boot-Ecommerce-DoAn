package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Cart;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CartRestController {
    private final Logger log = LoggerFactory.getLogger(CartRestController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;


    @PostMapping("/cart/update/{productId}/{quantity}")
    public Map<String, Object> updateQuantity(@PathVariable("productId") Integer productId,
                                              @PathVariable("quantity") Integer quantity,
                                              @AuthenticationPrincipal UserPrincipal loggedUser) {

        Map<String, Object> response = new HashMap<>();
        if(loggedUser != null) {
            try {
                User user = userService.getUserByID(loggedUser.getId());
                Product product = productService.getProductById(productId);

                if(quantity > product.getInStock()) {
                    response.put("message", "Can't change quantity this product. Quantity can't more than " + product.getInStock());
                    response.put("status", HttpStatus.BAD_REQUEST);
                    return response;
                }
                else if(quantity <= 0 ) {
                    response.put("message", "Can't change quantity this product. Quantity can't less than or equal to 0");
                    response.put("status", HttpStatus.BAD_REQUEST);
                    return response;
                }

                double subtotal = cartService.updatedQuantity(product, user, quantity);
                List<Cart> listCarts = cartService.findCartByUser(user.getId());

                double estimatedTotal = 0;

                for (Cart item : listCarts) {
                    estimatedTotal += item.getSubtotal();
                }

                response.put("estimatedTotal", estimatedTotal);
                response.put("subtotal", subtotal);
                response.put("status", HttpStatus.OK);

            } catch (UserNotFoundException e) {
                response.put("message", "You must login to change quantity of product.");
                response.put("status", HttpStatus.UNAUTHORIZED);
                return response;

            } catch (ProductNotFoundException e) {
                response.put("message", "Can't change quantity this product. Product not found.");
                response.put("status", HttpStatus.BAD_REQUEST);
                return response;
            }

        }else {
            response.put("message", "You must login to change quantity of product.");
            response.put("status", HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    @DeleteMapping("/cart/delete/{productId}")
    public Map<String, Object> updateQuantity(@PathVariable("productId") Integer productId,
                                              @AuthenticationPrincipal UserPrincipal loggedUser) {

        Map<String, Object> response = new HashMap<>();
        if(loggedUser != null) {
            try {
                User user = userService.getUserByID(loggedUser.getId());
                Product product = productService.getProductById(productId);

                cartService.deleteCartItem(user.getId(), product.getId());
                List<Cart> listCarts = cartService.findCartByUser(user.getId());

                double estimatedTotal = 0;

                for (Cart item : listCarts) {
                    estimatedTotal += item.getSubtotal();
                }

                response.put("estimatedTotal", estimatedTotal);
                response.put("status", HttpStatus.OK);

            } catch (UserNotFoundException e) {
                response.put("message", "You must login to delete this product.");
                response.put("status", HttpStatus.UNAUTHORIZED);
                return response;

            } catch (ProductNotFoundException e) {
                response.put("message", "Can't delete this product from cart. Product not found.");
                response.put("status", HttpStatus.BAD_REQUEST);
                return response;
            }

        }else {
            response.put("message", "You must login to delete this product.");
            response.put("status", HttpStatus.UNAUTHORIZED);
        }
        return response;
    }




}
