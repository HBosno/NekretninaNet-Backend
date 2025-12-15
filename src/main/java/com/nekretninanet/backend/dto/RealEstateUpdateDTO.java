package com.nekretninanet.backend.dto;

public class RealEstateUpdateDTO {
    private String title;
    private Double price;
    private String location;
    private Double area;
    private Integer yearBuilt;
    private String description;

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
}
