package com.nekretninanet.backend.dto;

public class ReviewRequestDTO {
    private Integer rating;
    private String comment;

    // Getteri i setteri
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}