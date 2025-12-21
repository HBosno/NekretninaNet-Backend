package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.*;

public class RealEstateCreateDTO {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Title can contain only letters, numbers, and spaces")
    private String title;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    @Max(value = 10_000_000, message = "Price cannot exceed 10,000,000")
    private Double price;
    @NotBlank(message = "Location cannot be blank")
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9. ]+$", message = "Location can contain only letters, numbers, spaces, and dots")
    private String location;
    @Positive(message = "Area must be positive")
    @Max(value = 10_000, message = "Area cannot exceed 10,000 m²")
    private Double area;
    @Min(value = 0, message = "Year built cannot be negative")
    @Max(value = 9999, message = "Year built cannot exceed 9999")
    private Integer yearBuilt;
    @Size(max = 550, message = "Description cannot exceed 550 characters")
    @Pattern(regexp = "^[A-Za-z0-9 ._?-]*$", message = "Description can contain letters, numbers, spaces, dot, question mark, dash, and underscore")
    private String description;
    @NotNull(message = "User ID cannot be null")
    private Long userId; // ID vlasnika

    // Konstruktor
    public RealEstateCreateDTO() {}

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

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}