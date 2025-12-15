package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.model.QueryStatus;
import com.nekretninanet.backend.model.QueryType;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

    // Enum-based methods
    List<Query> findByStatus(QueryStatus status);

    List<Query> findByQueryType(QueryType queryType);

    List<Query> findByUser(User user);

    List<Query> findByUserAndRealEstate(User user, RealEstate realEstate);

    List<Query> findByRealEstateIn(List<RealEstate> realEstates);

}
