package com.luv2code.doan.service;


import com.luv2code.doan.entity.Category;

import com.luv2code.doan.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    public static final int CATEGORY_PER_PAGE = 9;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> listByPage(Integer pageNum)
    {
        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE);

        return categoryRepository.findAll(pageable);
    }
}
