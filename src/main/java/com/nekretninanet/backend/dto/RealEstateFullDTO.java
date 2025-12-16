package com.nekretninanet.backend.dto;

import java.time.LocalDate;

public class RealEstateFullDTO {

    private Long id;
    private String title;
    private Double price;
    private String location;
    private Double area;
    private Integer yearBuilt;
    private String description;
    private LocalDate publishDate;
    private String status;

    public RealEstateFullDTO() {
    }

    public RealEstateFullDTO(
            Long id,
            String title,
            Double price,
            String location,
            Double area,
            Integer yearBuilt,
            String description,
            LocalDate publishDate,
            String status
    ) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.area = area;
        this.yearBuilt = yearBuilt;
        this.description = description;
        this.publishDate = publishDate;
        this.status = status;
    }

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public Double getArea() {
        return area;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public String getStatus() {
        return status;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
