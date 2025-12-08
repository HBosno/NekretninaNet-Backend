package com.nekretninanet.backend.service;

import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class QueryService {

    private final QueryRepository queryRepository;
    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;

    public QueryService(QueryRepository queryRepository,
                        RealEstateRepository realEstateRepository, UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.realEstateRepository = realEstateRepository;
        this.userRepository = userRepository;
    }

    public List<Query> getQueriesByStatus(String status) {
        return queryRepository.findByStatus(status);
    }

    public Query getQueryById(Long id) {
        return queryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public RealEstate getRealEstateById(Long id) {
        return realEstateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RealEstate not found"));
    }

    public Query saveQuery(Query query) {
        return queryRepository.save(query);
    }

    public List<Query> getQueriesForUserRealEstates(User user) {
    List<RealEstate> userEstates = realEstateRepository.findByUser(user);
    return queryRepository.findByRealEstateIn(userEstates);
}


    public Query updateQueryStatusAndResponse(Long id, String newStatus, String response) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Query not found"));

        query.setStatus(newStatus);
        query.setResponse(response);

        return queryRepository.save(query);
    }

    public Query createRealEstateQuery(User user, RealEstate realEstate, String question, String queryType) {
        Query query = new Query();
        query.setUser(user);
        query.setRealEstate(realEstate);
        query.setQuestion(question);
        query.setQueryType(queryType);
        query.setStatus("PENDING");
        query.setQueryDate(LocalDate.now());

        return queryRepository.save(query);
    }

    public Query createSupportQuery(User user, String question, String queryType) {
        Query query = new Query();
        query.setUser(user);
        query.setRealEstate(null);
        query.setQuestion(question);
        query.setQueryType(queryType);
        query.setStatus("PENDING");
        query.setQueryDate(LocalDate.now());

        return queryRepository.save(query);
    }

    public List<Query> getAllQueriesByUser(User user) {
        return queryRepository.findByUser(user);
    }

    public Query updateQuery(Long queryId, String newStatus, String response) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found"));

        query.setStatus(newStatus);
        query.setResponse(response);

        return queryRepository.save(query);
    }
}
