package com.luv2code.doan.repository;

import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>  {
    @Query("SELECT r FROM Review r WHERE r.product.id = :id ORDER BY r.date DESC")
    public Page<Review> findReviewByProduct(Integer id, Pageable pageable);

    @Query("SELECT count(r.id) FROM Review r WHERE r.product.id = :id AND r.vote = :starNum")
    public Integer countStarNumByProduct(Integer id, Integer starNum);

    @Query("SELECT count(r.id) from Review r WHERE r.id = :id")
    Long countById(Integer id);
}
