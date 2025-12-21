package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.*;

public class RealEstateUpdateDTO {
    @Size(max = 100, message = "Title cannot be longer than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ]*$", message = "Title can only contain letters, numbers and spaces")
    private String title;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    @DecimalMax(value = "1000000000", message = "Price is too high")
    private Double price;
    @Size(max = 50, message = "Location cannot be longer than 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9. ]*$", message = "Location can only contain letters, numbers, spaces and dots")
    private String location;
    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be positive")
    @DecimalMax(value = "1000000", message = "Area is too large")
    private Double area;
    @Min(value = 0, message = "Year built cannot be negative")
    @Max(value = 9999, message = "Year built is invalid")
    private Integer yearBuilt;
    @Size(max = 550, message = "Description cannot be longer than 550 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ._\\-?]*$", message = "Description contains invalid characters")
    private String description;
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status is invalid")
    private String status;

    public RealEstateUpdateDTO() {}

    // Getteri i setteri
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}