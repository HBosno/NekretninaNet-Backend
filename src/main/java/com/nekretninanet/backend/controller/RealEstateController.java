package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.RealEstateCreateDTO;
import com.nekretninanet.backend.dto.RealEstateDTO;
import com.nekretninanet.backend.dto.RealEstateStatusDTO;
import com.nekretninanet.backend.dto.RealEstateUpdateDTO;
import com.nekretninanet.backend.dto.RealEstateFullDTO;
import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.model.RealEstateStatus;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.service.RealEstateService;
import com.nekretninanet.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/real-estates")
public class RealEstateController {

    private final RealEstateService service;
    private final UserService userService;

    public RealEstateController(RealEstateService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<RealEstateDTO>> getRealEstates(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer yearBuilt
    ) {
        try {
            List<RealEstate> result;

            if (minPrice == null && maxPrice == null && location == null && yearBuilt == null) {
                result = service.getActiveRealEstates();
            } else {
                result = service.filterRealEstates(minPrice, maxPrice, location, yearBuilt);
            }

            if (result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<RealEstateDTO> dtoList = result.stream()
                    .map(r -> new RealEstateDTO(
                            r.getId(),
                            r.getTitle(),
                            r.getPrice(),
                            r.getLocation(),
                            r.getYearBuilt()))
                    .toList();

            return ResponseEntity.ok(dtoList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{title}")
    public ResponseEntity<List<RealEstateDTO>> getRealEstatesByTitle(@PathVariable String title) {
        try {
            List<RealEstate> estates = service.getRealEstatesByTitle(title);

            if (estates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<RealEstateDTO> dtoList = estates.stream()
                    .map(e -> new RealEstateDTO(
                            e.getId(),
                            e.getTitle(),
                            e.getPrice(),
                            e.getLocation(),
                            e.getYearBuilt()))
                    .toList();

            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RealEstateStatusDTO>> getRealEstatesByUserId(@PathVariable Long userId) {
        try {
            List<RealEstate> estates = service.getByUserId(userId);

            if (estates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            List<RealEstateStatusDTO> dtoList = estates.stream()
                    .map(e -> new RealEstateStatusDTO(
                            e.getId(),
                            e.getTitle(),
                            e.getPrice(),
                            e.getLocation(),
                            e.getYearBuilt(),
                            e.getStatus().name() // enum -> string
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/username/{username}")
    public ResponseEntity<List<RealEstateFullDTO>> getRealEstatesByUsername(@PathVariable String username) {
        try
        {
            List<RealEstate> estates = service.getByUsername(username);

            if (estates.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            List<RealEstateFullDTO> dtoList = estates.stream()
                .map(e -> new RealEstateFullDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getPrice(),
                        e.getLocation(),
                        e.getArea(),
                        e.getYearBuilt(),
                        e.getDescription(),
                        e.getPublishDate(),
                        e.getStatus().name()
                ))
                .toList();
            return ResponseEntity.ok(dtoList);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}


    @PostMapping
    public ResponseEntity<RealEstateStatusDTO> createRealEstate(@Valid @RequestBody RealEstateCreateDTO dto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("--------------ID trentnog usera: "+ userService.findByUsername(userDetails.getUsername()).getId());
            RealEstate realEstate = new RealEstate();
            realEstate.setTitle(dto.getTitle());
            realEstate.setPrice(dto.getPrice());
            realEstate.setLocation(dto.getLocation());
            realEstate.setArea(dto.getArea());
            realEstate.setYearBuilt(dto.getYearBuilt());
            realEstate.setDescription(dto.getDescription());
            realEstate.setPublishDate(LocalDate.now());
            realEstate.setStatus(RealEstateStatus.ACTIVE); // enum

            if (dto.getUserId() != null) {
                User user = service.getUserById(dto.getUserId());
                realEstate.setUser(user);
            }

            RealEstate created = service.createRealEstate(realEstate);

            RealEstateStatusDTO responseDTO = new RealEstateStatusDTO(
                    created.getId(),
                    created.getTitle(),
                    created.getPrice(),
                    created.getLocation(),
                    created.getYearBuilt(),
                    created.getStatus().name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   @PatchMapping("/{id}")
public ResponseEntity<RealEstateStatusDTO> updateRealEstatePartially(
        @PathVariable Long id,
        @Valid @RequestBody RealEstateUpdateDTO updates
) {
    try {
        RealEstate existing = service.getRealEstateById(id);

        if (updates.getTitle() != null)
            existing.setTitle(updates.getTitle());

        if (updates.getPrice() != null)
            existing.setPrice(updates.getPrice());

        if (updates.getLocation() != null)
            existing.setLocation(updates.getLocation());

        if (updates.getArea() != null)
            existing.setArea(updates.getArea());

        if (updates.getYearBuilt() != null)
            existing.setYearBuilt(updates.getYearBuilt());

        if (updates.getDescription() != null)
            existing.setDescription(updates.getDescription());

        // ✅ STATUS UPDATE (STRING → ENUM)
        if (updates.getStatus() != null) {
            try {
                existing.setStatus(
                        RealEstateStatus.valueOf(updates.getStatus().toUpperCase())
                );
            } catch (IllegalArgumentException ex) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }
        }

        RealEstate updated = service.updateRealEstatePartial(existing);

        RealEstateStatusDTO responseDTO = new RealEstateStatusDTO(
                updated.getId(),
                updated.getTitle(),
                updated.getPrice(),
                updated.getLocation(),
                updated.getYearBuilt(),
                updated.getStatus().name()
        );

        return ResponseEntity.ok(responseDTO);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRealEstate(@PathVariable Long id) {
        try {
            service.deleteRealEstateCascading(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting the real estate");
        }
    }
}
