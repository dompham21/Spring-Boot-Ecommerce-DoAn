package com.luv2code.doan.service;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);
    public static final int PRODUCT_PER_PAGE = 9;
    public static final int PRODUCT_SEARCH_PER_PAGE = 8;




    @Autowired
    private ProductRepository productRepository;

    public Product getProductByName(String name) {
        Product product = productRepository.getProductByName(name);

        return product;
    }


    public Product getProductById(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            return product;

        }
        catch(NoSuchElementException ex) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);

        }
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Page<Product> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {
        Pageable pageable = null;

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findAll(keyword, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long count = productRepository.countById(id);
        if (count == null || count == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        productRepository.deleteById(id);
    }

    public Page<Product> listLatestProduct() {
        Pageable pageable = PageRequest.of(0, 10);
        return productRepository.findLatestProduct(pageable);
    }

    public Page<Product> listBestSellProduct() {
        Pageable pageable = PageRequest.of(0,10);
        return productRepository.findBestSellProduct(pageable);
    }

    public Page<Product> listSearchProduct(String keyword, Integer pageNum, String radioPrice, String radioSort, String radioCategory, String radioBrand) {
        double minPrice = 0;
        double maxPrice = 0;
        Pageable pageable = null;
        if(radioPrice != null) {
            switch (radioPrice){
                case "from0to1":
                    minPrice = 0;
                    maxPrice = 1000000;
                    break;
                case "from1to2":

                    minPrice = 1000000;
                    maxPrice = 2000000;
                    break;
                case "from2to5":
                    minPrice = 2000000;
                    maxPrice = 5000000;
                    break;
                case "from5tomax":
                    minPrice = 5000000;
                    maxPrice = productRepository.getMaxPrice();
                    break;
                default:
                    minPrice = productRepository.getMinPrice();
                    maxPrice = productRepository.getMaxPrice();
            }
        }
        else {
            minPrice = productRepository.getMinPrice();
            maxPrice = productRepository.getMaxPrice();
        }


        if(radioSort != null ) {
            Sort sort = Sort.by("price");
            sort = radioSort.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, PRODUCT_SEARCH_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, PRODUCT_SEARCH_PER_PAGE);
        }

        if (keyword != null && !keyword.isEmpty()) {
            log.info("Search with keyword!");
            return productRepository.searchWithKeywordFilterProduct(keyword, minPrice, maxPrice, radioCategory, radioBrand, pageable);
        }

        return productRepository.searchFilterProduct(minPrice, maxPrice, radioCategory, radioBrand, pageable);

    }
}
