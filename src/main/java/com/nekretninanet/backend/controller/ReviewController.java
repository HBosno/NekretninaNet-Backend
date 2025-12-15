package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.ReviewDTO;
import com.nekretninanet.backend.dto.ReviewRequestDTO;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.Review;
import com.nekretninanet.backend.model.ReviewStatus;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;
import com.nekretninanet.backend.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public ReviewController(
            ReviewService reviewService,
            UserRepository userRepository,
            RealEstateRepository realEstateRepository
    ) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
    }

    /* ===================== SUPPORT ===================== */

    @GetMapping("/support/reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviewsDTO());
    }

    @DeleteMapping("/support/review/{id}")
    public ResponseEntity<Void> deleteReviewBySupport(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    /* ===================== USER ===================== */

    @PostMapping("/user/review/{userId}/{realEstateId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long userId,
            @PathVariable Long realEstateId,
            @RequestBody ReviewRequestDTO body
    ) {
        try {
            if (body.getRating() == null || body.getComment() == null || body.getComment().isBlank()) {
                return ResponseEntity.badRequest().body("Rating and comment are required");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            RealEstate realEstate = realEstateRepository.findById(realEstateId)
                    .orElseThrow(() -> new RuntimeException("Real estate not found"));

            Review review = new Review();
            review.setUser(user);
            review.setRealEstate(realEstate);
            review.setRating(body.getRating());
            review.setComment(body.getComment());
            review.setDate(LocalDate.now());
            review.setStatus(ReviewStatus.ACTIVE);

            Review saved = reviewService.saveReview(review);

            ReviewDTO response = new ReviewDTO(
                    saved.getRating(),
                    saved.getComment(),
                    saved.getDate(),
                    saved.getStatus().name(),
                    saved.getUser().getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating review");
        }
    }

    @PatchMapping("/user/review/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDTO body
    ) {
        try {
            Review review = reviewService.getReviewById(id)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            if (body.getRating() != null) {
                review.setRating(body.getRating());
            }
            if (body.getComment() != null && !body.getComment().isBlank()) {
                review.setComment(body.getComment());
            }

            review.setDate(LocalDate.now());

            Review updated = reviewService.saveReview(review);

            ReviewDTO response = new ReviewDTO(
                    updated.getRating(),
                    updated.getComment(),
                    updated.getDate(),
                    updated.getStatus().name(),
                    updated.getUser().getUsername()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating review");
        }
    }

    @DeleteMapping("/user/review/{id}")
    public ResponseEntity<?> deleteReviewByUser(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review successfully deleted");
    }

    @GetMapping("/user/real-estate/reviews/{id}")
    public ResponseEntity<List<Review>> getReviewsByRealEstateId(@PathVariable Long id) {
        List<Review> reviews = reviewService.getReviewsByRealEstateId(id);
        return reviews.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(reviews);
    }
}
