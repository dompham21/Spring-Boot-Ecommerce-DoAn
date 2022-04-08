package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("Select p from Product p WHERE p.name = :name")
    public Product getProductByName(String name);


    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.shortDescription LIKE %?1%")
    public Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT COUNT(p.id) from Product p WHERE p.id = :id")
    public Long countById(Integer id);

    @Query("SELECT p FROM Product  p WHERE p.isActive <> FALSE ORDER BY p.registrationDate DESC")
    public Page<Product> findLatestProduct(Pageable pageable);

    @Query("SELECT p FROM Product  p WHERE p.isActive <> FALSE ORDER BY p.soldQuantity DESC")
    public Page<Product> findBestSellProduct(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.price between :minPrice AND :maxPrice) AND p.isActive <> FALSE " +
            "AND ((:catId = 'all' AND p.categories.id is not null ) OR (:catId <> 'all' AND p.categories.id = :catId))" +
            "AND ((:brandId = 'all' AND p.brands.id is not null ) OR (:brandId <> 'all' AND p.brands.id = :brandId))")
    public Page<Product> searchFilterProduct(double minPrice, double maxPrice,String catId, String brandId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.shortDescription LIKE %:keyword%) AND " +
            "(p.price between :minPrice AND :maxPrice) AND p.isActive <> FALSE AND ((:catId = 'all' AND p.categories.id is not null ) OR (:catId <> 'all' AND p.categories.id = :catId))" +
            "AND ((:brandId = 'all' AND p.brands.id is not null ) OR (:brandId <> 'all' AND p.brands.id = :brandId))")
    public Page<Product> searchWithKeywordFilterProduct(String keyword, double minPrice, double maxPrice,String catId, String brandId, Pageable pageable);

    @Query("SELECT max(p.price) FROM Product  p")
    public double getMaxPrice();

    @Query("SELECT min(p.price) FROM Product  p")
    public double getMinPrice();
}
