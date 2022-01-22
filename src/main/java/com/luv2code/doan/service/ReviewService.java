package com.luv2code.doan.service;


import com.luv2code.doan.entity.Review;
import com.luv2code.doan.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



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
}
