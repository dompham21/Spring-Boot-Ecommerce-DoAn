package com.luv2code.doan.service;


import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryService {

    public static final int CATEGORY_PER_PAGE = 9;

    @Autowired
    private CategoryRepository categoryRepository;

    public Category getCategoryByName(String name) {
        Category category = categoryRepository.getCategoryByName(name);
        return category;
    }


    public Page<Category> listByPage(Integer pageNum, String keyword, String sortField, String sortDir) {
        Pageable pageable = null;

        if(sortField != null && !sortField.isEmpty()) {
            Sort sort = Sort.by(sortField);
            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
            pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE, sort);
        }
        else {
            pageable = PageRequest.of(pageNum - 1, CATEGORY_PER_PAGE);
        }

        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepository.findAll(keyword, pageable);
        }
        return categoryRepository.findAll(pageable);
    }

    public Category getCategoryById(Integer id) throws CategoryNotFoundException {
        try {
            Category category = categoryRepository.findById(id).get();
            return category;

        }
        catch(NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);

        }
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        Long count = categoryRepository.countById(id);
        if (count == null || count == 0) {
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        categoryRepository.deleteById(id);
    }


    public List<Category> findAllCategory() {
        return categoryRepository.findAll();
    }

    public List<Category> getTop5CategoryBestSell(){
        return  categoryRepository.top5CategoryBestSell();
    }
}
