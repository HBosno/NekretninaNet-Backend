package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {

    List<RealEstate> findByUser(User user);
    List<RealEstate> findByStatus(String status);

}
