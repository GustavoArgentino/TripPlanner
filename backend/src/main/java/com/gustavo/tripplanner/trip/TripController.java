package com.gustavo.tripplanner.trip;

import com.gustavo.tripplanner.trip.dto.TripRequest;
import com.gustavo.tripplanner.trip.dto.TripResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> create(Authentication authentication, @Valid @RequestBody TripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(authentication.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(tripService.listOwn(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> get(Authentication authentication, @PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getOwn(authentication.getName(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> update(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody TripRequest request
    ) {
        return ResponseEntity.ok(tripService.update(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
        tripService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
