package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.model.RealEstate;
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

import java.util.List;

@RestController
@RequestMapping("/real-estates")
public class RealEstateController {

    private final RealEstateService service;

    public RealEstateController(RealEstateService service) {
        this.service = service;
    }

    @GetMapping
public ResponseEntity<List<RealEstate>> getRealEstates(
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Integer yearBuilt
) {
    try {
        List<RealEstate> result;

        // Ako nema filter parametara → vrati sve aktivne
        if (minPrice == null && maxPrice == null && location == null && yearBuilt == null) {
            result = service.getActiveRealEstates();
        } 
        // Ako ima filter parametara → filtriraj
        else {
            result = service.filterRealEstates(minPrice, maxPrice, location, yearBuilt);
        }

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(result);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

@GetMapping("/{title}")
public ResponseEntity<List<RealEstate>> getRealEstatesByTitle(@PathVariable String title) {
    try {
        List<RealEstate> estates = service.getRealEstatesByTitle(title);

        if (estates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(estates);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

@GetMapping("/user/{username}")
public ResponseEntity<?> getRealEstatesByUsername(@PathVariable String username) {
    try {
        List<RealEstate> estates = service.getByUsername(username);

        if (estates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(estates); // 200 OK
    }
    catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    }
}

@PostMapping
public ResponseEntity<?> createRealEstate(@RequestBody RealEstate realEstate) {
    try {
        if (realEstate == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Request body is missing or invalid");
        }

        RealEstate created = service.createRealEstate(realEstate);

        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 CREATED
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while creating real estate.");
    }
}

@PatchMapping("/{id}")
public ResponseEntity<?> updateRealEstatePartially(
        @PathVariable Long id,
        @RequestBody RealEstate updates
) {
    try {
        RealEstate updated = service.updateRealEstatePartial(id, updates);

        return ResponseEntity.ok(updated); // 200 OK
    }
    catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
    }
    catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating real estate");
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<?> deleteRealEstate(@PathVariable Long id) {
    try {
        service.deleteRealEstate(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while deleting the real estate"); // 500 Internal Server Error
    }
}



}
