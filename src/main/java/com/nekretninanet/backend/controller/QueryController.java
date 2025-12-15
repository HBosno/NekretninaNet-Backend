package com.nekretninanet.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.dto.SupportRequestResponseDto;
import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.service.QueryService;
import com.nekretninanet.backend.view.QueryViews;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class QueryController {
    private final QueryService queryService;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public QueryController(QueryService queryService,
                           UserRepository userRepository,
                           RealEstateRepository realEstateRepository) {
        this.queryService = queryService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
    }

    @GetMapping("/support/requests")
    @JsonView(QueryViews.SupportRequestSummary.class)
    public ResponseEntity<List<Query>> getAllSupportRequests() {
        List<Query> requests = queryService.getAllSupportRequests();
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/support/request/{id}")
    public ResponseEntity<Query> respondToSupportRequest(
            @PathVariable Long id,
            @Valid @RequestBody SupportRequestResponseDto dto
    ) {
        Query updatedQuery = queryService.respondToSupportRequest(id, dto.getResponse());
        return ResponseEntity.ok(updatedQuery);
    }

    @PostMapping("/user/real-estate-query/{realEstateId}/{userId}")
    public ResponseEntity<?> createRealEstateQuery(
            @PathVariable Long realEstateId,
            @PathVariable Long userId,
            @RequestBody Query queryBody
    ) {
        try {
            if (queryBody.getQuestion() == null || queryBody.getQuestion().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Question cannot be empty");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            RealEstate realEstate = realEstateRepository.findById(realEstateId)
                    .orElseThrow(() -> new RuntimeException("RealEstate not found"));

            Query createdQuery = queryService.createRealEstateQuery(
                    user,
                    realEstate,
                    queryBody.getQuestion(),
                    queryBody.getQueryType()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuery);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating query");
        }
    }

    @GetMapping("/user/real-estate-queries/{username}")
    public ResponseEntity<?> getQueriesForUser(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Query> queries = queryService.getQueriesForUserRealEstates(user);

            if (queries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
            }

            return ResponseEntity.ok(queries); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving queries"); // 500
        }
    }

    @PatchMapping("/user/real-estate-queries/{id}")
    public ResponseEntity<?> updateQuery(@PathVariable Long id,
                                         @RequestBody Map<String, Object> updates) {
        try {
            Query query = queryService.getQueryById(id); // metoda koju ćemo dodati u service

            if (updates.containsKey("question")) {
                query.setQuestion((String) updates.get("question"));
            }
            if (updates.containsKey("response")) {
                query.setResponse((String) updates.get("response"));
            }
            if (updates.containsKey("status")) {
                query.setStatus((String) updates.get("status"));
            }
            if (updates.containsKey("queryType")) {
                query.setQueryType((String) updates.get("queryType"));
            }
            if (updates.containsKey("queryDate")) {
                query.setQueryDate(LocalDate.parse((String) updates.get("queryDate")));
            }
            // Za user i realEstate, obično šaljemo samo ID-e
            if (updates.containsKey("userId")) {
                Long userId = Long.valueOf(String.valueOf(updates.get("userId")));
                query.setUser(queryService.getUserById(userId));
            }
            if (updates.containsKey("realEstateId")) {
                Long realEstateId = Long.valueOf(String.valueOf(updates.get("realEstateId")));
                query.setRealEstate(queryService.getRealEstateById(realEstateId));
            }

            Query updatedQuery = queryService.saveQuery(query);
            return ResponseEntity.ok(updatedQuery); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating query");
        }
    }

    @PostMapping("/user/support-request/{userId}")
    public ResponseEntity<?> createSupportRequest(@PathVariable Long userId,
                                                  @RequestBody Map<String, String> body) {
        try {
            String question = body.get("question");
            String queryType = body.get("queryType");

            if (question == null || question.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Question is required");
            }

            User user = queryService.getUserById(userId);

            Query query = queryService.createSupportQuery(user, question, queryType);

            return ResponseEntity.status(HttpStatus.CREATED).body(query);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating support request");
        }
    }
}