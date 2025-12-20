package com.nekretninanet.backend.repository;

import com.nekretninanet.backend.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {
    List<Query> findByStatus(QueryStatus status);
    List<Query> findByUser(User user);
    List<Query> findByUserAndRealEstate(User user, RealEstate realEstate);
    List<Query> findByQueryType(QueryType queryType);
    List<Query> findByRealEstateIn(List<RealEstate> realEstate);
    List<Query> findByRealEstateIdIn(List<Long> ids);
}
