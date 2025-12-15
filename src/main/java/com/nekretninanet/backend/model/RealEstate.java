package com.nekretninanet.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "real_estate")
public class RealEstate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double price;

    private String location;

    private Double area;

    @Column(name = "year_built")
    private Integer yearBuilt;

    private String description;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Enumerated(EnumType.STRING)  // Ova anotacija omogućava da se enum mapira na string u bazi
    private RealEstateStatus status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    public RealEstate() {
    }

    public RealEstate(String title, Double price, String location, Double area, Integer yearBuilt, String description, LocalDate publishDate, RealEstateStatus status, User user) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.area = area;
        this.yearBuilt = yearBuilt;
        this.description = description;
        this.publishDate = publishDate;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public RealEstateStatus getStatus() {
        return status;
    }

    public void setStatus(RealEstateStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "RealEstate{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", area=" + area +
                ", yearBuilt=" + yearBuilt +
                ", description='" + description + '\'' +
                ", publishDate=" + publishDate +
                ", status=" + status +
                ", user=" + user +
                '}';
    }
}
