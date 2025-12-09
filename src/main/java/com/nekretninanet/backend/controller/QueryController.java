package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.SupportRequestResponseDto;
import com.nekretninanet.backend.model.Query;
import com.nekretninanet.backend.service.QueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class QueryController {
    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
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
}
