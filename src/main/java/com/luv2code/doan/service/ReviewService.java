package com.luv2code.doan.service;


import com.luv2code.doan.entity.Product;
import com.luv2code.doan.entity.Review;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.exceptions.ReviewNotFoundException;
import com.luv2code.doan.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Service
public class ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);
    public static final int REVIEW_PER_PAGE = 9;

    @Autowired
    private ReviewRepository reviewRepository;

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }


    public Page<Review> getReviewByProduct(Integer id, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEW_PER_PAGE);
        return reviewRepository.findReviewByProduct(id, pageable);
    }

    public Integer getCountStarNumByProduct(Integer id, Integer starNum) {
        return  reviewRepository.countStarNumByProduct(id, starNum);
    }

    public void deleteReview(Integer id) throws ReviewNotFoundException {
        Long count = reviewRepository.countById(id);
        if (count == null || count == 0) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }

        reviewRepository.deleteById(id);
    }

    public Review getReviewById(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).get();

        }
        catch(NoSuchElementException ex) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);

        }
    }

}
