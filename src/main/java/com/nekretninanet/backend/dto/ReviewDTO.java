package com.nekretninanet.backend.dto;

import java.time.LocalDate;

/*
    kreirano da se vracaju recenzije sa username korisnika koji su ostavili (bez ostalih user podataka i podatka za
    vezu sa realestate. endpoint GET /support/reviews iz reviewcontroller
*/
public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDate date;
    private String status;
    private String username;

    public ReviewDTO(Long id, Integer rating, String comment, LocalDate date, String status, String username) {
        this.id=id;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.status = status;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
