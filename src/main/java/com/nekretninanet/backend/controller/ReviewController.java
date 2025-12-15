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
import com.nekretninanet.backend.dto.ReviewRequestDTO;
import com.nekretninanet.backend.repository.ReviewRepository;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.Optional;
@RestController
@RequestMapping("/")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService,
                            UserRepository userRepository,
                            RealEstateRepository realEstateRepository,
                            ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
        this.reviewRepository = reviewRepository;
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
@PostMapping("/user/review/{userId}/{realEstateId}")
public ResponseEntity<?> createReview(
        @PathVariable Long userId,
        @PathVariable Long realEstateId,
        @RequestBody ReviewRequestDTO body
) {
    try {
        Integer rating = body.getRating();
        String comment = body.getComment();

        if (rating == null || comment == null || comment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Rating and comment are required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        RealEstate realEstate = realEstateRepository.findById(realEstateId)
                .orElseThrow(() -> new RuntimeException("Real estate not found"));

        Review review = new Review();
        review.setUser(user);
        review.setRealEstate(realEstate);
        review.setRating(rating);
        review.setComment(comment);
        review.setDate(LocalDate.now());  // automatski današnji datum
        review.setStatus("ACTIVE");       // inicijalni status

        Review savedReview = reviewRepository.save(review);

        // Kreiranje DTO za povrat
        ReviewDTO responseDTO = new ReviewDTO(
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getDate(),
                savedReview.getStatus(),
                savedReview.getUser().getUsername()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

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

        // Ažuriranje samo rating i comment
        if (body.getRating() != null) {
            review.setRating(body.getRating());
        }
        if (body.getComment() != null && !body.getComment().isEmpty()) {
            review.setComment(body.getComment());
        }

        // Automatski postavljamo današnji datum
        review.setDate(LocalDate.now());

        Review updatedReview = reviewService.saveReview(review);

        // Mapiranje u DTO za povrat
        ReviewDTO responseDTO = new ReviewDTO(
                updatedReview.getRating(),
                updatedReview.getComment(),
                updatedReview.getDate(),
                updatedReview.getStatus(),
                updatedReview.getUser().getUsername()
        );

        return ResponseEntity.ok(responseDTO);

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
        // Provjeri da li review postoji
        Optional<Review> reviewOptional = reviewService.getReviewById(id);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review with ID " + id + " not found.");
        }

        // Obriši review
        reviewService.deleteReview(id);

        return ResponseEntity.ok("Review successfully deleted");

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting review: " + e.getMessage());
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
