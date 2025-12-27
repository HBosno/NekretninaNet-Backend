package com.nekretninanet.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.ReviewViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(ReviewViews.SupportReviewSummary.class)
    private Long id;

    @JsonView(ReviewViews.SupportReviewSummary.class)
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;

    @JsonView(ReviewViews.SupportReviewSummary.class)
    @Size(max = 100, message = "Comment cannot be longer than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$", message = "Comment contains invalid characters")
    private String comment;

    @JsonView(ReviewViews.SupportReviewSummary.class)
    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @JsonView(ReviewViews.SupportReviewSummary.class)
    private ReviewStatus status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @NotNull(message = "User is required")
    @JsonView(ReviewViews.SupportReviewSummary.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "realEstateId", nullable = false)
    @NotNull(message = "Real estate is required")
    private RealEstate realEstate;

    public Review() {
    }

    public Review(
            Integer rating,
            String comment,
            LocalDate date,
            ReviewStatus status,
            User user,
            RealEstate realEstate
    ) {
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.status = status;
        this.user = user;
        this.realEstate = realEstate;
    }

    public Long getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
    }
}
