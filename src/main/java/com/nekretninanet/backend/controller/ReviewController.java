package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.ReviewDTO;
import com.nekretninanet.backend.model.Review;
import com.nekretninanet.backend.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.repository.UserRepository;
import com.nekretninanet.backend.repository.RealEstateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public ReviewController(ReviewService reviewService,
                            UserRepository userRepository,
                            RealEstateRepository realEstateRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
    }

    @GetMapping("/support/reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviewsDTO();
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/support/review/{id}")
    public ResponseEntity<Void> deleteReviewBySupport(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user/review")
    public ResponseEntity<?> createReview(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            Long realEstateId = Long.valueOf(body.get("realEstateId").toString());
            Integer rating = Integer.valueOf(body.get("rating").toString());
            String comment = (String) body.get("comment");

            if (rating == null || comment == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Rating and comment are required");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            RealEstate realEstate = realEstateRepository.findById(realEstateId)
                    .orElseThrow(() -> new RuntimeException("Real estate not found"));

            Review review = reviewService.createReview(user, realEstate, rating, comment);

            return ResponseEntity.status(HttpStatus.CREATED).body(review);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating review");
        }
    }

    @PatchMapping("/user/review/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Review review = reviewService.getReviewById(id)
                    .orElseThrow(() -> new RuntimeException("Review not found"));

            // Ažuriranje polja ako su prisutna u body-ju
            if (updates.containsKey("rating")) {
                review.setRating(Integer.valueOf(updates.get("rating").toString()));
            }
            if (updates.containsKey("comment")) {
                review.setComment((String) updates.get("comment"));
            }
            if (updates.containsKey("status")) {
                review.setStatus((String) updates.get("status"));
            }
            if (updates.containsKey("date")) {
                review.setDate(LocalDate.parse((String) updates.get("date")));
            }

            Review updatedReview = reviewService.saveReview(review);

            return ResponseEntity.ok(updatedReview);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating review");
        }
    }

    @DeleteMapping("/user/review/{id}")
    public ResponseEntity<?> deleteReviewByUser(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review successfully deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting review");
        }
    }

    @GetMapping("/user/real-estate/reviews/{id}")
    public ResponseEntity<List<Review>> getReviewsByRealEstateId(@PathVariable Long id) {
        try {
            List<Review> reviews = reviewService.getReviewsByRealEstateId(id);

            if (reviews.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
