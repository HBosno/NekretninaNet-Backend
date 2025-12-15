package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.*;
import com.nekretninanet.backend.repository.QueryRepository;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.ReviewRepository;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealEstateService {

    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;
    private final QueryRepository queryRepository;
    private final ReviewRepository reviewRepository;

    public RealEstateService(RealEstateRepository realEstateRepository,
                             UserRepository userRepository,
                             QueryRepository queryRepository,
                             ReviewRepository reviewRepository) {
        this.realEstateRepository = realEstateRepository;
        this.userRepository = userRepository;
        this.queryRepository = queryRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<RealEstate> getAllRealEstatesFiltered(Double minPrice, Double maxPrice,
                                                      String location, Double minArea, Double maxArea) {
        return realEstateRepository.findAll().stream()
                .filter(r -> minPrice == null || r.getPrice() >= minPrice)
                .filter(r -> maxPrice == null || r.getPrice() <= maxPrice)
                .filter(r -> location == null || r.getLocation().equalsIgnoreCase(location))
                .filter(r -> minArea == null || r.getArea() >= minArea)
                .filter(r -> maxArea == null || r.getArea() <= maxArea)
                .collect(Collectors.toList());
    }

    public List<RealEstate> filterRealEstates(Double minPrice, Double maxPrice, String location, Integer yearBuilt) {
        // Prosleđujemo enum RealEstateStatus.ACTIVE
        return realEstateRepository.filterRealEstates(minPrice, maxPrice, location, yearBuilt, RealEstateStatus.ACTIVE);
    }

    // Dohvat svih aktivnih nekretnina
    public List<RealEstate> getActiveRealEstates() {
        return realEstateRepository.findByStatus(RealEstateStatus.ACTIVE);
    }

    public List<RealEstate> getRealEstatesByTitle(String title) {
        return realEstateRepository.findByTitleContainingIgnoreCaseAndStatus(title, RealEstateStatus.ACTIVE);
    }

    public List<RealEstate> getByUsername(String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return realEstateRepository.findByUserUsername(username);
    }

    public List<RealEstate> getAllRealEstatesByUser(User user) {
        return realEstateRepository.findByUser(user);
    }

    // Kreiranje nove nekretnine
    public RealEstate createRealEstate(RealEstate realEstate) {
        realEstate.setPublishDate(LocalDate.now());
        realEstate.setStatus(RealEstateStatus.ACTIVE);
        return realEstateRepository.save(realEstate);
    }

    // Brisanje nekretnine
    public void deleteRealEstate(Long id) {
        if (!realEstateRepository.existsById(id)) {
            throw new RuntimeException("RealEstate not found");
        }
        realEstateRepository.deleteById(id);
    }

    // Ažuriranje nekretnine parcijalno
    public RealEstate updateRealEstatePartial(RealEstate updates) {
        RealEstate existing = getRealEstateById(updates.getId());

        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getPrice() != null) existing.setPrice(updates.getPrice());
        if (updates.getLocation() != null) existing.setLocation(updates.getLocation());
        if (updates.getArea() != null) existing.setArea(updates.getArea());
        if (updates.getYearBuilt() != null) existing.setYearBuilt(updates.getYearBuilt());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());

        return realEstateRepository.save(existing);
    }


    // Ažuriranje nekretnine sa id-em
    public RealEstate updateRealEstate(Long id, RealEstate newData) {
        RealEstate existing = getRealEstateById(id);

        existing.setTitle(newData.getTitle());
        existing.setPrice(newData.getPrice());
        existing.setLocation(newData.getLocation());
        existing.setArea(newData.getArea());
        existing.setYearBuilt(newData.getYearBuilt());
        existing.setDescription(newData.getDescription());
        existing.setStatus(newData.getStatus());

        return realEstateRepository.save(existing);
    }

    // Dohvat po ID-u
    public RealEstate getRealEstateById(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Real estate not found"));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<RealEstate> getByUserId(Long userId) {
        User user = getUserById(userId);
        return realEstateRepository.findByUser(user);
    }

    // Brisanje nekretnine sa kaskadnim brisanjem upita i recenzija
    public void deleteRealEstateCascading(Long id) {
        RealEstate realEstate = getRealEstateById(id);

        try {
            List<Query> queries = queryRepository.findByRealEstateIn(List.of(realEstate));
            queryRepository.deleteAll(queries);

            List<Review> reviews = reviewRepository.findByRealEstateId(id);
            reviewRepository.deleteAll(reviews);

            realEstateRepository.delete(realEstate);

        } catch (Exception e) {
            throw new RuntimeException("Error during cascading delete: " + e.getMessage());
        }
    }
}
