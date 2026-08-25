package com.gustavo.tripplanner.itinerary;

import com.gustavo.tripplanner.auth.AppUserDetailsService;
import com.gustavo.tripplanner.auth.JwtService;
import com.gustavo.tripplanner.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItineraryItemController.class)
@Import(SecurityConfig.class)
class ItineraryItemControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItineraryItemService itineraryItemService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    private final UUID tripId = UUID.randomUUID();

    private void mockValidToken(String email) {
        when(jwtService.isValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn(email);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("hashed-password")
                .roles("USER")
                .build();
        when(appUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
    }

    @Test
    void rejectsListWithoutToken() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId + "/itinerary-items"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsCreateWithoutToken() throws Exception {
        mockMvc.perform(post("/api/trips/" + tripId + "/itinerary-items")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsGetWithoutToken() throws Exception {
        mockMvc.perform(get("/api/trips/" + tripId + "/itinerary-items/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsUpdateWithoutToken() throws Exception {
        mockMvc.perform(put("/api/trips/" + tripId + "/itinerary-items/" + UUID.randomUUID())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsDeleteWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/trips/" + tripId + "/itinerary-items/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mapsTripNotFoundToNotFound() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);
        when(itineraryItemService.list(email, tripId))
                .thenThrow(new com.gustavo.tripplanner.trip.TripNotFoundException());

        mockMvc.perform(get("/api/trips/" + tripId + "/itinerary-items").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mapsItineraryItemNotFoundToNotFound() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);
        UUID itemId = UUID.randomUUID();
        when(itineraryItemService.get(email, tripId, itemId)).thenThrow(new ItineraryItemNotFoundException());

        mockMvc.perform(get("/api/trips/" + tripId + "/itinerary-items/" + itemId).header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mapsInvalidItineraryDateToBadRequest() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);
        when(itineraryItemService.create(eq(email), eq(tripId), any())).thenThrow(new InvalidItineraryDateException());

        mockMvc.perform(post("/api/trips/" + tripId + "/itinerary-items")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{\"title\":\"Passeio\",\"date\":\"2026-09-20\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsCreateWithMissingRequiredFields() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);

        mockMvc.perform(post("/api/trips/" + tripId + "/itinerary-items")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
