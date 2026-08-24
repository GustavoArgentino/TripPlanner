package com.gustavo.tripplanner.auth;

import com.gustavo.tripplanner.auth.dto.AuthResponse;
import com.gustavo.tripplanner.auth.dto.LoginRequest;
import com.gustavo.tripplanner.auth.dto.RegisterRequest;
import com.gustavo.tripplanner.user.User;
import com.gustavo.tripplanner.user.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        User user = new User(email, passwordEncoder.encode(request.password()), request.name());
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Two concurrent registrations raced past the existsByEmail check above;
            // the DB's unique constraint on email is the real source of truth.
            throw new EmailAlreadyInUseException(email);
        }

        return AuthResponse.bearer(jwtService.generateToken(user.getEmail()));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        return AuthResponse.bearer(jwtService.generateToken(email));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
