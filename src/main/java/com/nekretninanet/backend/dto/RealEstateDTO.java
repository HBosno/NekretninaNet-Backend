package com.nekretninanet.backend.dto;

public class RealEstateDTO {
    private Long id;
    private String title;
    private Double price;
    private String location;
    private Integer yearBuilt;

    public RealEstateDTO(Long id, String title, Double price, String location, Integer yearBuilt) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.yearBuilt = yearBuilt;
    }

    // Getteri i setteri
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getYearBuilt() { return yearBuilt; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }
}
