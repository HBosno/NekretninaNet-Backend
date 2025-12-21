package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.*;

public class ReviewRequestDTO {
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;
    @Size(max = 100, message = "Comment cannot be longer than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9 .,!?\\-()]*$", message = "Comment contains invalid characters")
    private String comment;

    // Getteri i setteri
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}