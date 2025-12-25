package com.nekretninanet.backend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.nekretninanet.backend.dto.QueryResponseDTO;
import com.nekretninanet.backend.dto.SupportRequestDTO;
import com.nekretninanet.backend.dto.QueryResponseLongDTO;
import com.nekretninanet.backend.dto.SupportRequestResponseDto;
import com.nekretninanet.backend.model.*;
import com.nekretninanet.backend.repository.QueryRepository;
import com.nekretninanet.backend.service.QueryService;
import com.nekretninanet.backend.service.UserService;
import com.nekretninanet.backend.util.SanitizeUtil;
import com.nekretninanet.backend.view.QueryViews;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import java.util.stream.Collectors;
import java.util.Collections;
@RestController
@RequestMapping("/")
public class QueryController {

    private final QueryService queryService;
    private final UserService userService;
    private final QueryRepository queryRepository;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public QueryController(QueryService queryService, UserService userService,
                           UserRepository userRepository,
                           RealEstateRepository realEstateRepository,
                           QueryRepository queryRepository) {
        this.queryService = queryService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
        this.queryRepository = queryRepository;
    }

    /* ===================== SUPPORT ===================== */

    @GetMapping("/support/requests")
    @PreAuthorize("hasRole('SUPPORT')")
    @JsonView(QueryViews.SupportRequestSummary.class)
    public ResponseEntity<List<Query>> getAllSupportRequests() {
        List<Query> requests = queryService.getAllSupportRequests();
        return ResponseEntity.ok(requests);
    }
    @JsonView(QueryViews.SupportRequestResponseSummary.class)
    @PatchMapping("/support/request/{id}")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<Query> respondToSupportRequest(
            @PathVariable Long id,
            @Valid @RequestBody SupportRequestResponseDto dto
    ) {
        Query updatedQuery = queryService.respondToSupportRequest(id, dto.getResponse());
        return ResponseEntity.ok(updatedQuery);
    }

    /* ===================== USER ===================== */

   @PostMapping("/user/real-estate-query/{realEstateId}")
   @PreAuthorize("hasRole('USER')")
public ResponseEntity<?> createRealEstateQuery(
        @PathVariable Long realEstateId,
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody String question
) {
    try {
        User user = userService.findByUsername(userDetails.getUsername());

        if (question == null || question.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question cannot be empty");
        }

        if (question.length() > 500) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question cannot exceed 500 characters");
        }

        if (!question.matches("^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question contains invalid characters");
        }

        RealEstate realEstate = realEstateRepository.findById(realEstateId)
                .orElseThrow(() -> new RuntimeException("RealEstate not found"));

        if (realEstate.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You cannot send a query to your own property");
        }

        if (realEstate.getStatus() == RealEstateStatus.INACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot send a query for an inactive property");
        }

        question = question.replace("\"", "").trim();

        Query newQuery = new Query();
        newQuery.setUser(user);
        newQuery.setRealEstate(realEstate);
        newQuery.setQuestion(SanitizeUtil.sanitize(question));
        newQuery.setQueryDate(LocalDate.now());
        newQuery.setResponse(""); // default
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


    @GetMapping("/user/real-estate-queries/{realEstateId}")
    @PreAuthorize("hasRole('USER')")
public ResponseEntity<List<QueryResponseLongDTO>> getQueriesForUser(@PathVariable Long realEstateId) {
    try {
        RealEstate realEstate = realEstateRepository.findById(realEstateId)
                .orElseThrow(() -> new RuntimeException("Real estate not found"));

        List<Query> queries = queryRepository.findByRealEstateAndStatusNot(realEstate, QueryStatus.REMOVED);

        if (queries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

List<QueryResponseLongDTO> dtoList = queries.stream()
        .map(q -> new QueryResponseLongDTO(
                q.getId(),                // Long id
                q.getQueryDate(),         // LocalDate queryDate
                q.getQuestion(),          // String question
                q.getResponse(),          // String response
                q.getQueryType().name(),  // String queryType
                q.getStatus().name(),     // String status
                q.getUser().getId(),      // Long userId
                q.getUser().getUsername() // String username
        ))
        .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
}


  @PatchMapping("/user/real-estate-queries/{id}")
  @PreAuthorize("hasRole('USER')")
public ResponseEntity<?> updateQuery(
        @PathVariable Long id,
        @RequestBody String response // direktno očekujemo String u body-u
) {
    try {
        Query query = queryService.getQueryById(id);

        // Provjera tipa upita
        if (query.getQueryType() == QueryType.SUPPORT_REQUEST) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Cannot update a support request with this endpoint");
        }

        // Ako je response poslan i nije prazan, ažuriraj ga i postavi status na ANSWERED
        if (response != null && !response.isBlank()) {
            if (response.length() > 500) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Response cannot exceed 500 characters");
            }
            if (!response.matches("^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Response contains invalid characters");
            }

            query.setResponse(SanitizeUtil.sanitize(response));
            query.setStatus(QueryStatus.ANSWERED);
        }

        // Ostale stvari uvijek ažuriramo
        query.setQueryDate(LocalDate.now());
        query.setQueryType(QueryType.REAL_ESTATE_QUERY);

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


    @PostMapping("/user/support-request")
    @PreAuthorize("hasRole('USER')")
public ResponseEntity<?> createSupportRequest(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody String question
) {
    try {
        User user = userService.findByUsername(userDetails.getUsername());

        if (question == null || question.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question is required");
        }

        if (question.length() > 500) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question cannot exceed 500 characters");
        }

        if (!question.matches("^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question contains invalid characters");
        }

        Query query = new Query();
        query.setUser(user);
        query.setQuestion(SanitizeUtil.sanitize(question));
        query.setQueryDate(LocalDate.now());
        query.setResponse(null); // default
        query.setQueryType(QueryType.SUPPORT_REQUEST);
        query.setStatus(QueryStatus.UNANSWERED);
        query.setRealEstate(null); // support request nije vezan za nekretninu

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
