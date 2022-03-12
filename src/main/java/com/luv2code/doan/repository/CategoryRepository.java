package com.luv2code.doan.repository;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT p FROM Category p WHERE p.name LIKE %:keyword% "
            + "OR p.description LIKE %:keyword% ")
    public Page<Category> findAll(String keyword, Pageable pageable);
}