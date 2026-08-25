package com.gustavo.tripplanner.itinerary;

import com.gustavo.tripplanner.itinerary.dto.ItineraryItemRequest;
import com.gustavo.tripplanner.itinerary.dto.ItineraryItemResponse;
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
@RequestMapping("/api/trips/{tripId}/itinerary-items")
public class ItineraryItemController {

    private final ItineraryItemService itineraryItemService;

    public ItineraryItemController(ItineraryItemService itineraryItemService) {
        this.itineraryItemService = itineraryItemService;
    }

    @PostMapping
    public ResponseEntity<ItineraryItemResponse> create(
            Authentication authentication,
            @PathVariable UUID tripId,
            @Valid @RequestBody ItineraryItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itineraryItemService.create(authentication.getName(), tripId, request));
    }

    @GetMapping
    public ResponseEntity<List<ItineraryItemResponse>> list(Authentication authentication, @PathVariable UUID tripId) {
        return ResponseEntity.ok(itineraryItemService.list(authentication.getName(), tripId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItineraryItemResponse> get(
            Authentication authentication,
            @PathVariable UUID tripId,
            @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(itineraryItemService.get(authentication.getName(), tripId, itemId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ItineraryItemResponse> update(
            Authentication authentication,
            @PathVariable UUID tripId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ItineraryItemRequest request
    ) {
        return ResponseEntity.ok(itineraryItemService.update(authentication.getName(), tripId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable UUID tripId,
            @PathVariable UUID itemId
    ) {
        itineraryItemService.delete(authentication.getName(), tripId, itemId);
        return ResponseEntity.noContent().build();
    }
}
