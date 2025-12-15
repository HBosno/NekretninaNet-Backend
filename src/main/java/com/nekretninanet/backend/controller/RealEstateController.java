package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.model.RealEstate;
import com.nekretninanet.backend.dto.RealEstateDTO;
import com.nekretninanet.backend.dto.RealEstateStatusDTO;
import com.nekretninanet.backend.dto.RealEstateCreateDTO;
import com.nekretninanet.backend.dto.RealEstateUpdateDTO;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.service.RealEstateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.stream.Collectors;


import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/real-estates")
public class RealEstateController {

    private final RealEstateService service;

    public RealEstateController(RealEstateService service) {
        this.service = service;
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

        // Mapiraj u DTO
        List<RealEstateDTO> dtoList = result.stream()
                .map(r -> new RealEstateDTO(r.getId(), r.getTitle(), r.getPrice(), r.getLocation(), r.getYearBuilt()))
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

        // Mapiraj u DTO
        List<RealEstateDTO> dtoList = estates.stream()
                .map(e -> new RealEstateDTO(e.getId(), e.getTitle(), e.getPrice(), e.getLocation(), e.getYearBuilt()))
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
                        e.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}

@GetMapping("/user/{username}")
public ResponseEntity<List<RealEstateStatusDTO>> getRealEstatesByUsername(@PathVariable String username) {
    try {
        List<RealEstate> estates = service.getByUsername(username);

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
                        e.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}


@PostMapping
public ResponseEntity<RealEstateStatusDTO> createRealEstate(@RequestBody RealEstateCreateDTO dto) {
    try {
        // Napravi RealEstate entitet
        RealEstate realEstate = new RealEstate();
        realEstate.setTitle(dto.getTitle());
        realEstate.setPrice(dto.getPrice());
        realEstate.setLocation(dto.getLocation());
        realEstate.setArea(dto.getArea());
        realEstate.setYearBuilt(dto.getYearBuilt());
        realEstate.setDescription(dto.getDescription());
        realEstate.setPublishDate(LocalDate.now()); // danasnji datum
        realEstate.setStatus("ACTIVE");

        // Poveži korisnika
        if (dto.getUserId() != null) {
            User user = service.getUserById(dto.getUserId()); // dodaj ovu metodu u service
            realEstate.setUser(user);
        }

        RealEstate created = service.createRealEstate(realEstate);

        // Mapiraj u DTO za povrat
        RealEstateStatusDTO responseDTO = new RealEstateStatusDTO(
                created.getId(),
                created.getTitle(),
                created.getPrice(),
                created.getLocation(),
                created.getYearBuilt(),
                created.getStatus()
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
        @RequestBody RealEstateUpdateDTO updates
) {
    try {
        // Dohvati postojeću nekretninu
        RealEstate existing = service.getRealEstateById(id);

        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getPrice() != null) existing.setPrice(updates.getPrice());
        if (updates.getLocation() != null) existing.setLocation(updates.getLocation());
        if (updates.getArea() != null) existing.setArea(updates.getArea());
        if (updates.getYearBuilt() != null) existing.setYearBuilt(updates.getYearBuilt());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());

        RealEstate updated = service.updateRealEstatePartial(existing);

        // Mapiranje u DTO
        RealEstateStatusDTO responseDTO = new RealEstateStatusDTO(
                updated.getId(),
                updated.getTitle(),
                updated.getPrice(),
                updated.getLocation(),
                updated.getYearBuilt(),
                updated.getStatus()
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
        return ResponseEntity.noContent().build(); // 204 No Content
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while deleting the real estate"); // 500
    }
}




}
