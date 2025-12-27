package com.nekretninanet.backend.service;

import com.nekretninanet.backend.dto.ReviewDTO;
import com.nekretninanet.backend.exception.ResourceNotFoundException;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.Review;
import com.nekretninanet.backend.model.ReviewStatus;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.model.ReviewStatus;
import com.nekretninanet.backend.repository.ReviewRepository;
import com.nekretninanet.backend.util.SanitizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No reviews found.");
        }
        return reviews;
    }

    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewsByUsername(String username) {
        List<Review> reviews = reviewRepository.findByUserUsername(username);
        if (reviews == null) {
            return new ArrayList<>();
        }
        return reviews;
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID " + id + " not found."));

 //       review.setStatus(ReviewStatus.REMOVED);  ipak cemo HARD delete, za soft treba azurirat i sva kaskadna brisanja..
 //       reviewRepository.save(review);           ovako manje sanse za greske
        reviewRepository.delete(review);
    }

    public Optional<Review> getReviewById(Long id) {
    return reviewRepository.findById(id);
}

    public Review saveReview(Review review) {
        if (review.getComment() != null) {
            review.setComment(SanitizeUtil.sanitize(review.getComment()));
        }
        return reviewRepository.save(review);
    }

    public Review createReview(User user, RealEstate realEstate, Integer rating, String comment) {
        Review review = new Review();
        review.setUser(user);
        review.setRealEstate(realEstate);
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now());
        review.setStatus(ReviewStatus.ACTIVE);

        return reviewRepository.save(review);
    }

    public List<ReviewDTO> getActiveReviewsByRealEstateId(Long realEstateId) {
        return reviewRepository.findByRealEstateId(realEstateId).stream()
                .filter(r -> r.getStatus() == ReviewStatus.ACTIVE)
                .map(r -> new ReviewDTO(
                        r.getId(),
                        r.getRating(),
                        r.getComment(),
                        r.getDate(),
                        r.getStatus().name(),
                        r.getUser().getUsername()
                ))
                .toList();
    }

    public Review updateReview(Long reviewId, Integer rating, String comment, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (rating != null) review.setRating(rating);
        if (comment != null) review.setComment(comment);
        if (status != null) review.setStatus(status);

        return reviewRepository.save(review);
    }
}
