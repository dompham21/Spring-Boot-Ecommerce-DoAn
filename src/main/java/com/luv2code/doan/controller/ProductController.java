package com.luv2code.doan.controller;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;


    @GetMapping("/admin/product/add")
    public String addProduct(Model model) {
        Product product = new Product();
        product.setIsActive(true);
        model.addAttribute("product", product);

        return "product/new_product";
    }

    @GetMapping("/admin/product")
    public String listFirstPage() {
        return "redirect:/admin/product/page/1";
    }

    @GetMapping("/admin/product/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String sortField,
                             @RequestParam(required = false) String sortDir) {

        if(sortField != null) {
            model.addAttribute("sortField", sortField);
        }
        if(sortDir == null) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        if(keyword != null) {
            model.addAttribute("keyword", keyword);
        }

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
    public String saveProduct(@Valid Product product, BindingResult errors, RedirectAttributes redirectAttributes) {

        if(productService.getProductByName(product.getName()) != null) {
            errors.rejectValue("name", "product", "Ten san pham khong duoc trung!");
        }
        if(errors.hasErrors()) {
            log.info("has errors");
            return "product/new_product";
        }

        else {
            product.setRegistrationDate(new Date());
            product.setSoldQuantity(0);
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
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("isEdit", true);
            model.addAttribute("productName", product.getName());
            return "product/new_product";
        }
        catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/product/page/1";

        }
    }

    @PostMapping("/admin/product/edit/{id}")
    public String saveEditProduct(@Valid Product product, BindingResult errors, RedirectAttributes redirectAttributes,
                                  @PathVariable("id") Integer id) {

        try {
            Product existProduct = productService.getProductById(id);

            Product productCheckUnique = productService.getProductByName(product.getName());

            if ((!product.getName().equals(existProduct.getName())) && (productCheckUnique != null)) {
                errors.rejectValue("name", "product", "Ten san pham khong duoc trung!");
            }
            if (errors.hasErrors()) {
                log.info("has errors");
                return "product/new_product";
            } else {

                existProduct.setName(product.getName());
                existProduct.setIsActive(product.getIsActive());
                existProduct.setPrice(product.getPrice());
                existProduct.setInStock(product.getInStock());
                existProduct.setDescription(product.getDescription());
                existProduct.setShortDescription(product.getShortDescription());
                existProduct.setDiscount(product.getDiscount());
                existProduct.setImage(product.getImage());
//                    existProduct.setBrands(product.getBrands());
//                    existProduct.setCategories(product.getCategories());

                log.info(existProduct.toString());
                productService.saveProduct(existProduct);

                redirectAttributes.addFlashAttribute("messageSuccess", "The product has been edited successfully.");
                return "redirect:/admin/product/page/1";


            }
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("messageError", e.getMessage());
            return "redirect:/admin/product/page/1";
        }
    }

}
