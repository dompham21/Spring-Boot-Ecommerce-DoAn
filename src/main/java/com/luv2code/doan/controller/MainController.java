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
    private BrandService brandService;

    @Autowired
    private PosterService posterService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        Page<Product> page = productService.listLatestProduct();
        Page<Product> pageBestSell = productService.listBestSellProduct();
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
        List<Brand> listBrands = brandService.getAllBrand();
        List<Category> listCategories = categoryService.findAllCategory();
        List<Poster> listPostersLeft = posterService.listPosterLeftUser();
        List<Poster> listPostersRight = posterService.listPosterRightUser();

        List<Product> listLatestProducts = page.getContent();
        List<Product> listBestSellProducts = pageBestSell.getContent();

        model.addAttribute("listPostersLeft", listPostersLeft);
        model.addAttribute("listPostersRight", listPostersRight);
        model.addAttribute("listLatestProducts", listLatestProducts);
        model.addAttribute("listBestSellProducts", listBestSellProducts);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);

        return "index";
    }

    @GetMapping("/search")
    public String searchPage(Model model, @AuthenticationPrincipal UserPrincipal loggedUser) {
        return searchFilterPage(1, "", "all", "all", "all", null, loggedUser, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchFilterPage(@PathVariable(name = "pageNum") Integer pageNum,
                                   @RequestParam(name = "keywordSearch", defaultValue = "") String keywordSearch,
                                   @RequestParam(name = "radio-category", defaultValue = "all") String radioCategory,
                                   @RequestParam(name = "radio-brand", defaultValue = "all") String radioBrand,
                                   @RequestParam(name = "radio-price", defaultValue = "all") String radioPrice,
                                   @RequestParam(name = "radio-sort", required = false) String radioSort,
                                   @AuthenticationPrincipal UserPrincipal loggedUser, Model model) {

        System.out.println(radioBrand);
        System.out.println(radioCategory);
        Page<Product> page = productService.listSearchProduct(keywordSearch, pageNum, radioPrice, radioSort, radioCategory, radioBrand);
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
        List<Category> listCategories = categoryService.getTop5CategoryBestSell();
        List<Brand> listBrands = brandService.getTop5BrandBestSell();
        long startCount = (pageNum - 1) * productService.PRODUCT_SEARCH_PER_PAGE + 1;
        long endCount = startCount +  productService.PRODUCT_SEARCH_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }


        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);

        model.addAttribute("keywordSearch", keywordSearch);
        model.addAttribute("radioCategory", radioCategory);
        model.addAttribute("radioBrand", radioBrand);
        model.addAttribute("radioSort", radioSort);
        model.addAttribute("radioPrice", radioPrice);
        model.addAttribute("listProducts", listProducts);


        return "search";
    }


}
