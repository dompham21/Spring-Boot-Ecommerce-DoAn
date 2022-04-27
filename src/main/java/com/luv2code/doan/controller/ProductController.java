package com.luv2code.doan.controller;


import com.amazonaws.services.dynamodbv2.xspec.S;
import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CartService cartService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;



    @GetMapping("/admin/product/add")
    public String addProduct(Model model) {
        List<Brand> listBrands = brandService.getAllBrand();
        List<Category> listCategories = categoryService.findAllCategory();

        Product product = new Product();
        product.setIsActive(true);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("listCategories", listCategories);
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", product);
        }

        return "product/new_product";
    }

    @GetMapping("/admin/product")
    public String listFirstPage() {
        return "redirect:/admin/product/page/1";
    }

    @GetMapping("/admin/product/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "id") String sortField,
                             @RequestParam(required = false) String sortDir) {

        model.addAttribute("sortField", sortField);

        if(sortDir == null) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        model.addAttribute("keyword", keyword);


        Page<Product> page = productService.listByPage(pageNum, keyword, sortField, sortDir);
        List<Product> listProducts = page.getContent();
        long startCount = (pageNum - 1) * productService.PRODUCT_PER_PAGE + 1;
        long endCount = startCount +  productService.PRODUCT_PER_PAGE - 1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }



        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);

        model.addAttribute("listProducts", listProducts);


        return "product/products";
    }

    @PostMapping("/admin/product/add")
    public String saveProduct(Product product, BindingResult errors, RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file) throws StorageUploadFileException, IOException {


        if(product.getName().trim().length() == 0) {
            errors.rejectValue("name", "product", "Vui lòng nhập tên sản phẩm !");
        }
        if(productService.getProductByName(product.getName()) != null) {
            errors.rejectValue("name", "product", "Tên sản phẩm không được trùng!");
        }
        if(product.getPrice() == null) {
            errors.rejectValue("price", "product", "Vui lòng nhập đơn giá !");
        }
        else if(product.getPrice() <= 0) {
            errors.rejectValue("price", "product", "Đơn giá phải lớn hơn 0 !");
        }
        if(product.getShortDescription().trim().length() == 0) {
            errors.rejectValue("shortDescription", "product", "Short description không được bỏ trống!");
        }
        if(product.getDescription().trim().length() == 0) {
            errors.rejectValue("description", "product", "Description không được bỏ trống!");
        }
        if(product.getDiscount() == null) {
            product.setDiscount(0);
        }
        if(product.getDiscount()  < 0 || product.getDiscount() > 100 ) {
            errors.rejectValue("discount", "product", "Discount  phải lon hon hoac bang 0 va be hon hoac bang 100!");
        }
        if(product.getInStock() == null) {
            errors.rejectValue("inStock", "product", "In stock không được bỏ trống!");
        }
        else if(product.getInStock() < 0) {
            errors.rejectValue("inStock", "product", "Số lượng phải nhiều hơn hoac bang 0 !");
        }

        if(product.getImage() == null) {
            errors.rejectValue("image", "product", "Image không được bỏ trống!");
        }

        if(errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.product", errors);
            redirectAttributes.addFlashAttribute("product", product);
            return "redirect:/admin/product/add";
        }

        else {
              String url = storageService.upload(file);
              System.out.println(url);
              product.setSoldQuantity(0);
              product.setRegistrationDate(new Date());
              product.setImage(url);
              productService.saveProduct(product);

              redirectAttributes.addFlashAttribute("messageSuccess", "The product has been saved successfully.");
              return "redirect:/admin/product/page/1";

        }


    }

    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("messageSuccess", "The product ID " + id + " has been deleted successfully");
        }
        catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
        }
        return "redirect:/admin/product/page/1";
    }


    @GetMapping("/admin/product/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id,RedirectAttributes redirectAttributes, Model model) {
        try {
            List<Brand> listBrands = brandService.getAllBrand();
            List<Category> listCategories = categoryService.findAllCategory();
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("listCategories", listCategories);
            if (!model.containsAttribute("product")) {
                Product product = productService.getProductById(id);
                model.addAttribute("product", product);
            }
            return "product/new_product";
        }
        catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/product/page/1";

        }
    }

    @PostMapping("/admin/product/edit/{id}")
    public String saveEditProduct(Product product, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {

        try {
            Product existProduct = productService.getProductById(id);
            Product productCheckUnique = productService.getProductByName(product.getName());

            if ( productCheckUnique != null && !productCheckUnique.getId().equals(existProduct.getId())) {
                errors.rejectValue("name", "product", "Tên sản phẩm không được trùng!");
            }
            if(product.getName().trim().length() == 0) {
                errors.rejectValue("name", "product", "Vui lòng nhập tên sản phẩm !");
            }
            if(product.getPrice() == null) {
                errors.rejectValue("price", "product", "Vui lòng nhập đơn giá !");
            }
            else if(product.getPrice() <= 0) {
                errors.rejectValue("price", "product", "Đơn giá phải lớn hơn 0 !");
            }
            if(product.getShortDescription().trim().length() == 0) {
                errors.rejectValue("shortDescription", "product", "Short description không được bỏ trống!");
            }
            if(product.getDescription().trim().length() == 0) {
                errors.rejectValue("description", "product", "Description không được bỏ trống!");
            }
            if(product.getDiscount() == null) {
                product.setDiscount(0);
            }
            if(product.getDiscount()  < 0 || product.getDiscount() > 100 ) {
                errors.rejectValue("discount", "product", "Discount  phải lon hon hoac bang 0 va be hon hoac bang 100!");
            }
            if(product.getInStock() == null) {
                errors.rejectValue("inStock", "product", "In stock không được bỏ trống!");
            }
            else if(product.getInStock() < 0) {
                errors.rejectValue("inStock", "product", "Số lượng phải nhiều hơn hoac bang 0 !");
            }

            if(product.getImage() == null) {
                errors.rejectValue("image", "product", "Image không được bỏ trống!");
            }
            if (errors.hasErrors()) {
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.product", errors);
                redirectAttributes.addFlashAttribute("product", product);
                return "redirect:/admin/product/edit/" + id;
            } else {
                if(!existProduct.getImage().equals(product.getImage())) {
                    String url = storageService.upload(file);
                    product.setImage(url);
                }


                product.setSoldQuantity(existProduct.getSoldQuantity());
                product.setRegistrationDate(existProduct.getRegistrationDate());




                log.info(product.toString());
                productService.saveProduct(product);

                redirectAttributes.addFlashAttribute("messageSuccess", "The product has been edited successfully.");
                return "redirect:/admin/product/page/1";


            }
        } catch (ProductNotFoundException | IOException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/product/page/1";
        }
    }

    @GetMapping("/product/detail/{id}")
    public String detailProduct(@PathVariable("id") Integer id,RedirectAttributes redirectAttributes, Model model,
                                @AuthenticationPrincipal UserPrincipal loggedUser) {
        try {
            Product product = productService.getProductById(id);
            Page<Review> page = reviewService.getReviewByProduct(id, 1);
            List<Review> listReviews = page.getContent();

            if(loggedUser != null) {
                List<Cart> listCarts = cartService.findCartByUser(loggedUser.getId());
                log.info(listCarts.isEmpty() + "");
                boolean isUserBuyProduct = orderService.isUserHasBuyProduct(loggedUser.getId(), id);



                double estimatedTotal = 0;

                for (Cart item : listCarts) {
                    estimatedTotal += item.getSubtotal();

                }

                log.info(estimatedTotal + "");
                model.addAttribute("isUserBuyProduct", isUserBuyProduct);

                model.addAttribute("listCarts", listCarts);
                model.addAttribute("estimatedTotal", estimatedTotal);
            }
            if (!model.containsAttribute("review")) {
                Review review = new Review();
                model.addAttribute("review", review);
            }
            int pageNum = 1;
            long startCount = (pageNum - 1) * reviewService.REVIEW_PER_PAGE + 1;
            long endCount = startCount +  reviewService.REVIEW_PER_PAGE - 1;

            if(endCount > page.getTotalElements()) {
                endCount = page.getTotalElements();
            }
            int countStarOne = reviewService.getCountStarNumByProduct(id, 1);
            int countStarTwo = reviewService.getCountStarNumByProduct(id, 2);
            int countStarThree = reviewService.getCountStarNumByProduct(id, 3);
            int countStarFour = reviewService.getCountStarNumByProduct(id, 4);
            int countStarFive = reviewService.getCountStarNumByProduct(id, 5);

            float overall = (5 * countStarFive + 4 * countStarFour + 3 * countStarThree + 2 * countStarTwo + countStarOne) /(float) page.getTotalElements();
            if(Float.isNaN(overall)) {
                overall = 0;
            }


            model.addAttribute("countStarOne", countStarOne);
            model.addAttribute("countStarTwo", countStarTwo);
            model.addAttribute("countStarThree", countStarThree);
            model.addAttribute("countStarFour", countStarFour);
            model.addAttribute("countStarFive", countStarFive);
            model.addAttribute("overall", overall);



            model.addAttribute("product", product);

            model.addAttribute("listReviews", listReviews);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("currentPage", pageNum);
            return "product/detail_product";
        }
        catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "product/detail_product";

        }
    }

}
