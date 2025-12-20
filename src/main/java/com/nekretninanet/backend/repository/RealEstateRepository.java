package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.RealEstateStatus;
import com.nekretninanet.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    List<RealEstate> findByUser(User user);
    List<RealEstate> findByStatus(RealEstateStatus status);
    List<RealEstate> findByUserUsername(String username);

    // Custom filter query
    @Query("SELECT r FROM RealEstate r WHERE " +
            "(:minPrice IS NULL OR r.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.price <= :maxPrice) AND " +
            "(:location IS NULL OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:yearBuilt IS NULL OR r.yearBuilt = :yearBuilt) AND " +
            "r.status = :status")
    List<RealEstate> filterRealEstates(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("location") String location,
            @Param("yearBuilt") Integer yearBuilt,
            @Param("status") RealEstateStatus status
    );
    List<RealEstate> findByTitleContainingIgnoreCaseAndStatus(String title, RealEstateStatus status);
}
