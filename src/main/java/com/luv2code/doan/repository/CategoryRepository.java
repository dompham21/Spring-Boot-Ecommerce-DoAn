package com.luv2code.doan.repository;


import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("SELECT p FROM Category p WHERE p.name LIKE %:keyword% "
            + "OR p.description LIKE %:keyword% ")
    public Page<Category> findAll(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p.id) from Category p WHERE p.id = :id")
    public Long countById(Integer id);

    @Query("Select c from Category c WHERE c.name = :name")
    public Category getCategoryByName(String name);

    @Query(value = "select id, description, image, name From (select category_id, sum(sold_quantity) AS sell FROM products \n" +
            "group by category_id\n" +
            "having sum(sold_quantity)\n" +
            "order by sell desc limit 0,5)\n" +
            "as listCat inner join categories as cat on listCat.category_id = cat.id", nativeQuery = true)
    public List<Category> top5CategoryBestSell();
}