package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
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

    public RealEstateService (RealEstateRepository realEstateRepository, UserRepository userRepository){
        this.realEstateRepository=realEstateRepository;
        this.userRepository=userRepository;
    }

    public List<RealEstate> filterRealEstates(Double minPrice, Double maxPrice, String location, Integer yearBuilt){
        return realEstateRepository.filterRealEstates(minPrice,maxPrice,location,yearBuilt);
    }

    public List<RealEstate> getActiveRealEstates(){
        return realEstateRepository.findByStatus("ACTIVE");
    }

    public List<RealEstate> getRealEstatesByTitle(String title) {
    return realEstateRepository.findByTitleContainingIgnoreCaseAndStatus(title, "ACTIVE");
}

public List<RealEstate> getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return realEstateRepository.findByUserUsername(username);
    }

    public List<RealEstate> getAllRealEstatesByUser(User user) {
        return realEstateRepository.findByUser(user);
    }

    public RealEstate createRealEstate(RealEstate realEstate) {
        realEstate.setPublishDate(LocalDate.now());
        realEstate.setStatus("ACTIVE");
        return realEstateRepository.save(realEstate);
    }

    public void deleteRealEstate(Long id) {
        if (!realEstateRepository.existsById(id)) {
            throw new RuntimeException("RealEstate not found");
        }
        realEstateRepository.deleteById(id);
    }

    public RealEstate updateRealEstatePartial(Long id, RealEstate updates) {
    RealEstate existing = realEstateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Real estate not found"));

    if (updates.getTitle() != null) {
        existing.setTitle(updates.getTitle());
    }

    if (updates.getPrice() != null) {
        existing.setPrice(updates.getPrice());
    }

    if (updates.getLocation() != null) {
        existing.setLocation(updates.getLocation());
    }

    if (updates.getArea() != null) {
        existing.setArea(updates.getArea());
    }

    if (updates.getYearBuilt() != null) {
        existing.setYearBuilt(updates.getYearBuilt());
    }

    if (updates.getDescription() != null) {
        existing.setDescription(updates.getDescription());
    }

    if (updates.getStatus() != null) {
        existing.setStatus(updates.getStatus());
    }

    return realEstateRepository.save(existing);
}


    public RealEstate updateRealEstate(Long id, RealEstate newData) {
        RealEstate existing = realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RealEstate not found"));

        existing.setTitle(newData.getTitle());
        existing.setPrice(newData.getPrice());
        existing.setLocation(newData.getLocation());
        existing.setArea(newData.getArea());
        existing.setYearBuilt(newData.getYearBuilt());
        existing.setDescription(newData.getDescription());
        existing.setStatus(newData.getStatus());

        return realEstateRepository.save(existing);
    }
}
