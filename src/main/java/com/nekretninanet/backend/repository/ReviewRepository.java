package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);
    List<Review> findByUserUsername(String username);
    List<Review> findByRealEstateId(Long realEstateId);
}
