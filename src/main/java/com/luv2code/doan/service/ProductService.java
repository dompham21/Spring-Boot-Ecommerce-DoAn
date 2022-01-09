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

import java.util.NoSuchElementException;

@Service
public class ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductService.class);
    public static final int PRODUCT_PER_PAGE = 9;



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
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        product.setPrice(Double.valueOf(decimalFormat.format(product.getPrice())));


        return productRepository.save(product);
    }

    public Page<Product> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE, sort);

            if (keyword != null && !keyword.isEmpty()) {
                return productRepository.findAll(keyword, pageable);
            }
            return productRepository.findAll(pageable);
        }
        else {
            Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);

            if (keyword != null && !keyword.isEmpty()) {
                log.info("search with keyword" + keyword);
                return productRepository.findAll(keyword, pageable);

            }
            return productRepository.findAll(pageable);
        }
    }

    public void deleteProduct(Integer id) throws ProductNotFoundException {
        Long count = productRepository.countById(id);
        if (count == null || count == 0) {
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        productRepository.deleteById(id);
    }
}
