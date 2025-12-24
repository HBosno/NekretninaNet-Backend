package com.nekretninanet.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.dto.ReviewDTO;
import com.nekretninanet.backend.dto.ReviewRequestDTO;
import com.nekretninanet.backend.model.Review;
import com.nekretninanet.backend.model.ReviewStatus;
import com.nekretninanet.backend.service.ReviewService;
import com.nekretninanet.backend.service.UserService;
import com.nekretninanet.backend.view.ReviewViews;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public ReviewController(
            ReviewService reviewService,
            UserService userService,
            UserRepository userRepository,
            RealEstateRepository realEstateRepository
    ) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
    }

    /* ===================== SUPPORT ===================== */

    @GetMapping("/support/reviews")
    @JsonView(ReviewViews.SupportReviewSummary.class)
    public ResponseEntity<List<Review>> getReviewsByUsername(
            @RequestParam String username) {
        List<Review> reviews = reviewService.getReviewsByUsername(username);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/support/review/{id}")
    public ResponseEntity<Void> deleteReviewBySupport(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    /* ===================== USER ===================== */

    @PostMapping("/user/review/{realEstateId}")
    public ResponseEntity<?> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long realEstateId,
            @Valid @RequestBody ReviewRequestDTO body
    ) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());

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
                    saved.getId(),
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
            @Valid @RequestBody ReviewRequestDTO body
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
                    updated.getId(),
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
    public ResponseEntity<List<ReviewDTO>> getReviewsByRealEstateId(@PathVariable Long id) {
        List<ReviewDTO> reviews = reviewService.getActiveReviewsByRealEstateId(id);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }
}
