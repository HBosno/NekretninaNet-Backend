package com.nekretninanet.backend.dto;

public class RealEstateStatusDTO {
    private Long id;
    private String title;
    private Double price;
    private String location;
    private Integer yearBuilt;
    private String status;

    public RealEstateStatusDTO(Long id, String title, Double price, String location, Integer yearBuilt, String status) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.yearBuilt = yearBuilt;
        this.status = status;
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}