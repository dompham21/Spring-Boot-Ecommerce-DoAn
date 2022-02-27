package com.luv2code.doan.controller;



import com.luv2code.doan.entity.Address;
import com.luv2code.doan.entity.Cart;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Province;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.AddressService;
import com.luv2code.doan.service.CartService;
import com.luv2code.doan.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;


    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        return "admin";
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        Page<Product> page = productService.listLatestProduct();

        List<Product> listLatestProducts = page.getContent();
        if(loggedUser != null) {
            List<Cart> listCarts = cartService.findCartByUser(loggedUser.getId());
            log.info(listCarts.isEmpty() + "");

            double estimatedTotal = 0;

            for (Cart item : listCarts) {
                estimatedTotal += item.getSubtotal();

            }

            log.info(estimatedTotal + "");
            model.addAttribute("listCarts", listCarts);
            model.addAttribute("estimatedTotal", estimatedTotal);
        }
        model.addAttribute("listLatestProducts", listLatestProducts);

        return "index";
    }





}
