package com.gustavo.tripplanner.trip;

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

@WebMvcTest(TripController.class)
@Import(SecurityConfig.class)
class TripControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TripService tripService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

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
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsCreateWithoutToken() throws Exception {
        mockMvc.perform(post("/api/trips")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsGetWithoutToken() throws Exception {
        mockMvc.perform(get("/api/trips/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsUpdateWithoutToken() throws Exception {
        mockMvc.perform(put("/api/trips/" + UUID.randomUUID())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsDeleteWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/trips/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mapsTripNotFoundToNotFound() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);
        UUID tripId = UUID.randomUUID();
        when(tripService.getOwn(email, tripId)).thenThrow(new TripNotFoundException());

        mockMvc.perform(get("/api/trips/" + tripId).header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mapsInvalidTripDatesToBadRequest() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);
        when(tripService.create(eq(email), any())).thenThrow(new InvalidTripDatesException());

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{\"name\":\"Trip\",\"destination\":\"Dest\",\"startDate\":\"2026-09-10\",\"endDate\":\"2026-09-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsCreateWithMissingRequiredFields() throws Exception {
        String email = "user@example.com";
        mockValidToken(email);

        mockMvc.perform(post("/api/trips")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
