package com.gustavo.tripplanner.user;

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

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void rejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsRequestWithValidToken() throws Exception {
        String email = "user@example.com";
        when(jwtService.isValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn(email);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("hashed-password")
                .roles("USER")
                .build();
        when(appUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        User user = new User(email, "hashed-password", "Test User");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
