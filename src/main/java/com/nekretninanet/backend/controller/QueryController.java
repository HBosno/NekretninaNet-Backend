package com.nekretninanet.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.dto.QueryResponseDTO;
import com.nekretninanet.backend.dto.SupportRequestDTO;
import com.nekretninanet.backend.dto.SupportRequestResponseDto;
import com.nekretninanet.backend.model.*;
import com.nekretninanet.backend.repository.QueryRepository;
import com.nekretninanet.backend.service.QueryService;
import com.nekretninanet.backend.view.QueryViews;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.model.QueryStatus;
import com.nekretninanet.backend.model.QueryType;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class QueryController {

    private final QueryService queryService;
    private final QueryRepository queryRepository;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public QueryController(QueryService queryService,
                           UserRepository userRepository,
                           RealEstateRepository realEstateRepository,
                           QueryRepository queryRepository) {
        this.queryService = queryService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
        this.queryRepository = queryRepository;
    }

    /* ===================== SUPPORT ===================== */

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

    /* ===================== USER ===================== */

    @PostMapping("/user/real-estate-query/{realEstateId}/{userId}")
    public ResponseEntity<?> createRealEstateQuery(
            @PathVariable Long realEstateId,
            @PathVariable Long userId,
            @RequestBody Query queryBody
    ) {
        try {
            if (queryBody.getQuestion() == null || queryBody.getQuestion().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Question cannot be empty");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            RealEstate realEstate = realEstateRepository.findById(realEstateId)
                    .orElseThrow(() -> new RuntimeException("RealEstate not found"));

            Query newQuery = new Query();
            newQuery.setUser(user);
            newQuery.setRealEstate(realEstate);
            newQuery.setQuestion(queryBody.getQuestion());
            newQuery.setQueryDate(LocalDate.now());
            newQuery.setResponse(""); // prazno
            newQuery.setQueryType(QueryType.REAL_ESTATE_QUERY);
            newQuery.setStatus(QueryStatus.UNANSWERED);

            Query createdQuery = queryRepository.save(newQuery);

            QueryResponseDTO responseDTO = new QueryResponseDTO(
                    createdQuery.getId(),
                    createdQuery.getQuestion(),
                    createdQuery.getQueryDate(),
                    createdQuery.getResponse(),
                    createdQuery.getQueryType().name(),
                    createdQuery.getStatus().name(),
                    createdQuery.getUser().getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

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
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(queries);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving queries");
        }
    }

    @PatchMapping("/user/real-estate-queries/{id}")
    public ResponseEntity<?> updateQuery(@PathVariable Long id,
                                         @RequestBody Map<String, String> body) {
        try {
            Query query = queryService.getQueryById(id);

            String question = body.get("question");
            if (question != null && !question.isBlank()) {
                query.setQuestion(question);
            }

            query.setQueryDate(LocalDate.now());
            if (query.getResponse() == null) query.setResponse("");
            query.setQueryType(QueryType.REAL_ESTATE_QUERY);
            query.setStatus(QueryStatus.UNANSWERED);

            Query updatedQuery = queryService.saveQuery(query);

            QueryResponseDTO responseDTO = new QueryResponseDTO(
                    updatedQuery.getId(),
                    updatedQuery.getQuestion(),
                    updatedQuery.getQueryDate(),
                    updatedQuery.getResponse(),
                    updatedQuery.getQueryType().name(),
                    updatedQuery.getStatus().name(),
                    updatedQuery.getUser().getUsername()
            );

            return ResponseEntity.ok(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating query");
        }
    }

    @PostMapping("/user/support-request/{userId}")
    public ResponseEntity<?> createSupportRequest(@PathVariable Long userId,
                                                  @RequestBody Map<String, String> body) {
        try {
            String question = body.get("question");
            if (question == null || question.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Question is required");
            }

            User user = queryService.getUserById(userId);

            Query query = new Query();
            query.setUser(user);
            query.setQuestion(question);
            query.setQueryDate(LocalDate.now());
            query.setResponse(null);
            query.setQueryType(QueryType.SUPPORT_REQUEST);
            query.setStatus(QueryStatus.UNANSWERED);
            query.setRealEstate(null);

            Query savedQuery = queryRepository.save(query);

            SupportRequestDTO responseDTO = new SupportRequestDTO(
                    savedQuery.getId(),
                    savedQuery.getQuestion(),
                    savedQuery.getQueryDate(),
                    savedQuery.getResponse(),
                    savedQuery.getQueryType().name(),
                    savedQuery.getStatus().name(),
                    savedQuery.getUser().getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating support request");
        }
    }

}
