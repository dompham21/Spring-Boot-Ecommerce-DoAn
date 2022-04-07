package com.luv2code.doan.service;

import com.luv2code.doan.controller.BrandController;
import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.BrandNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrand()
    {
        return brandRepository.findAll();
    }

    public Brand getBrandByName(String name) {
        Brand brand = brandRepository.getBrandByName(name);
        return brand;
    }

    public Brand getBrandById(Integer id) throws BrandNotFoundException {
        try {
            Brand brand = brandRepository.findById(id).get();
            return brand;

        }
        catch(NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);

        }
    }

    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }



}
