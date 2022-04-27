package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    @Query("Select p from Brand p WHERE p.name = :name")
    public Brand getBrandByName(String name);

    @Query(value = "select id, description, logo, name From (select brand_id, sum(sold_quantity) AS sell FROM products \n" +
            "group by brand_id\n" +
            "having sum(sold_quantity)\n" +
            "order by sell desc limit 0,2)\n" +
            "as listBrand inner join brands as bd on listBrand.brand_id = bd.id", nativeQuery = true)
    public List<Brand> getTop5BrandBestSell();

    @Query("SELECT COUNT(p.id) from Brand p WHERE p.id = :id")
    public Long countById(Integer id);

    @Query("SELECT p FROM Brand p WHERE p.name LIKE %:keyword% "
            + "OR p.description LIKE %:keyword% ")
    public Page<Brand> findAll(String keyword, Pageable pageable);
}
