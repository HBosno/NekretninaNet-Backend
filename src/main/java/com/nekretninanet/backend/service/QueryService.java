package com.nekretninanet.backend.service;

import com.nekretninanet.backend.exception.ResourceNotFoundException;
import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.QueryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    public List<Query> getQueriesByStatus(String status) {
        return queryRepository.findByStatus(status);
    }

    public List<Query> getAllSupportRequests() {
        List<Query> requests = queryRepository.findByQueryType("support-request");

        if (requests == null || requests.isEmpty()) {
            throw new ResourceNotFoundException("No support requests found.");
        }

        return requests;
    }

    @Transactional
    public Query respondToSupportRequest(Long id, String response) {
        Query query = queryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support request not found with id: " + id));

        if (!"support-request".equals(query.getQueryType())) {
            throw new IllegalArgumentException("Query is not a support request");
        }

        query.setResponse(response);
        query.setStatus("CLOSED"); // dogovoriti se po potrebi za statuse

        return queryRepository.save(query);
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
