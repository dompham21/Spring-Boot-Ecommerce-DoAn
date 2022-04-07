package com.luv2code.doan.controller;



import com.luv2code.doan.entity.*;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private StorageService storageService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private PosterService posterService;


    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        Page<Product> page = productService.listLatestProduct();

        List<Brand> listBrands = brandService.getAllBrand();
        List<Poster> listPostersLeft = posterService.listPosterLeft();
        List<Poster> listPostersRight = posterService.listPosterRight();

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
        model.addAttribute("listPostersLeft", listPostersLeft);
        model.addAttribute("listPostersRight", listPostersRight);
        model.addAttribute("listLatestProducts", listLatestProducts);
        model.addAttribute("listBrands", listBrands);
        return "index";
    }

    @GetMapping("/search")
    public String searchPage(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {

        Page<Product> page = productService.listLatestProduct();

        List<Product> listProducts = page.getContent();
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
        model.addAttribute("listProducts", listProducts);
        return "search";
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchFilterPage(@PathVariable(name = "pageNum") Integer pageNum,
                                   @RequestParam(name = "keywordSearch", defaultValue = "") String keywordSearch,
                                   @RequestParam(name = "radio-category", required = false) String radioCategory,
                                   @RequestParam(name = "radio-brand", required = false) String radioBrand,
                                   @RequestParam(name = "radio-price", required = false) String radioPrice,
                                   @RequestParam(name = "radio-sort", required = false) String radioSort,
                                   @AuthenticationPrincipal UserPrincipal loggedUser, Model model) {

        Page<Product> page = productService.listSearchProduct(keywordSearch, pageNum, radioPrice, radioSort);
        List<Product> listProducts = page.getContent();
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

        model.addAttribute("keywordSearch", keywordSearch);
        model.addAttribute("radioSort", radioSort);
        model.addAttribute("radioPrice", radioPrice);
        model.addAttribute("listProducts", listProducts);


        return "search";
    }



    @GetMapping("/api/test")
    public String test() {
        return "test";
    }

    @PostMapping("/api/test")
    public String postTest(@RequestParam("file") MultipartFile file) throws IOException {
        storageService.upload(file);
        return "test";
    }

}
