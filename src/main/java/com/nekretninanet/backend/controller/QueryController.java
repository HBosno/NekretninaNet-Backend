package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.SupportRequestResponseDto;
import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.service.QueryService;
import com.nekretninanet.backend.dto.QueryResponseDTO;
import com.nekretninanet.backend.dto.SupportRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.repository.RealEstateRepository;
import com.nekretninanet.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import com.nekretninanet.backend.repository.QueryRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/")
public class QueryController {
    private final QueryService queryService;
    private final QueryRepository queryRepository;
    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;

    public QueryController(QueryService queryService,
                           UserRepository userRepository,
                           RealEstateRepository realEstateRepository, QueryRepository queryRepository) {
        this.queryService = queryService;
        this.userRepository = userRepository;
        this.realEstateRepository = realEstateRepository;
        this.queryRepository = queryRepository;
    }

    @GetMapping("/support/requests")
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

        // Kreiranje upita sa automatskim poljima
        Query newQuery = new Query();
        newQuery.setUser(user);
        newQuery.setRealEstate(realEstate);
        newQuery.setQuestion(queryBody.getQuestion());
        newQuery.setQueryDate(LocalDate.now());       // danas
        newQuery.setResponse("");                     // prazno
        newQuery.setQueryType("real-estate-query");  // predefinisano
        newQuery.setStatus("neodgovoreno");          // predefinisano

        Query createdQuery = queryRepository.save(newQuery);

        // Mapiranje u DTO
        QueryResponseDTO responseDTO = new QueryResponseDTO(
                createdQuery.getId(),
                createdQuery.getQuestion(),
                createdQuery.getQueryDate(),
                createdQuery.getResponse(),
                createdQuery.getQueryType(),
                createdQuery.getStatus(),
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
                                     @RequestBody Map<String, String> body) {
    try {
        Query query = queryService.getQueryById(id); // dohvat postojećeg upita

        // Ažuriranje question iz body-ja
        String question = body.get("question");
        if (question != null && !question.isEmpty()) {
            query.setQuestion(question);
        }

        // Ostala polja se ažuriraju automatski isto kao kod POST za real-estate query
        query.setQueryDate(LocalDate.now());       // danas
        if (query.getResponse() == null) {
            query.setResponse("");                  // prazno ako nije postavljeno
        }
        query.setQueryType("real-estate-query");  // predefinisano
        query.setStatus("neodgovoreno");          // predefinisano

        Query updatedQuery = queryService.saveQuery(query);

        // Mapiranje u DTO
        QueryResponseDTO responseDTO = new QueryResponseDTO(
                updatedQuery.getId(),
                updatedQuery.getQuestion(),
                updatedQuery.getQueryDate(),
                updatedQuery.getResponse(),
                updatedQuery.getQueryType(),
                updatedQuery.getStatus(),
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

        if (question == null || question.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Question is required");
        }

        User user = queryService.getUserById(userId);

        // Kreiranje support upita
        Query query = new Query();
        query.setUser(user);
        query.setQuestion(question);
        query.setQueryDate(LocalDate.now());     // danas
        query.setResponse(null);                  // prazno
        query.setQueryType("support-request");   // predefinisano
        query.setStatus("neodgovoreno");         // predefinisano
        query.setRealEstate(null);               // nema nekretnine

        Query savedQuery = queryRepository.save(query);

        // Mapiranje u DTO za povrat
        SupportRequestDTO responseDTO = new SupportRequestDTO(
                savedQuery.getId(),
                savedQuery.getQuestion(),
                savedQuery.getQueryDate(),
                savedQuery.getResponse(),
                savedQuery.getQueryType(),
                savedQuery.getStatus(),
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