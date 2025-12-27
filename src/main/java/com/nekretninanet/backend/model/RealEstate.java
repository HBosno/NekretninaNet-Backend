package com.nekretninanet.backend.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.view.QueryViews;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "real_estate")
public class RealEstate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonView(QueryViews.SupportRequestSummary.class)
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title can have max 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9 čćžšđČĆŽŠĐ]+$", message = "Title can contain letters, numbers, and spaces")
    private String title;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    @Max(value = 1_000_000_000, message = "Price is too high")
    @Column(nullable = false)
    private Double price;
    @NotBlank(message = "Location cannot be blank")
    @Size(max = 50, message = "Location can have max 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9. čćžšđČĆŽŠĐ]+$", message = "Location can contain letters, numbers, spaces and dots")
    private String location;
    @Positive(message = "Area must be positive")
    @Max(value = 10_000, message = "Area is too large")
    private Double area;
    @Min(value = 0, message = "Year built cannot be negative")
    @Max(value = 9999, message = "Year built must be at most 9999")
    @Column(name = "year_built")
    private Integer yearBuilt;
    @Size(max = 550, message = "Description can have max 550 characters")
    @Pattern(regexp = "^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$", message = "Description contains invalid characters")
    private String description;

    @NotNull(message = "Publish date cannot be null")
    @FutureOrPresent(message = "Publish date cannot be in the past")
    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;
    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)  // Ova anotacija omogućava da se enum mapira na string u bazi
    private RealEstateStatus status;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @NotNull(message = "User cannot be null")
    @JsonView(QueryViews.SupportRequestSummary.class)
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
