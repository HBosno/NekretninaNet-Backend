package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.Review;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByUsername(String username) {
        return reviewRepository.findByUserUsername(username);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    public Optional<Review> getReviewById(Long id) {
    return reviewRepository.findById(id);
}

public Review saveReview(Review review) {
    return reviewRepository.save(review);
}




    public Review createReview(User user, RealEstate realEstate, Integer rating, String comment) {
        Review review = new Review();
        review.setUser(user);
        review.setRealEstate(realEstate);
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now());
        review.setStatus("PENDING");
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByRealEstateId(Long realEstateId) {
        return reviewRepository.findByRealEstateId(realEstateId);
    }

    public Review updateReview(Long reviewId, Integer rating, String comment, String status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (rating != null) review.setRating(rating);
        if (comment != null) review.setComment(comment);
        if (status != null) review.setStatus(status);

        return reviewRepository.save(review);
    }
}
