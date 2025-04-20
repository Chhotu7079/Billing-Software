package com.chhotu.billing_software.controller;

import com.chhotu.billing_software.io.AuthRequest;
import com.chhotu.billing_software.io.AuthResponse;
import com.chhotu.billing_software.service.UserService;
import com.chhotu.billing_software.service.impl.AppUserDetailsServiceImpl;
import com.chhotu.billing_software.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder; // For encoding and verifying passwords
    private final AuthenticationManager authenticationManager; // Used to authenticate credentials
    private final AppUserDetailsServiceImpl appUserDetailsService; // Custom user details service
    private final JwtUtil jwtUtil; // Utility class for generating JWT tokens
    private final UserService userService; // Service to fetch user-related information (like roles)



//    Handles user login by authenticating credentials and generating a JWT token.
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) throws Exception {
        authenticate(request.getEmail(), request.getPassword()); // Validate credentials

        // Load user details after authentication
        final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());

        // Generate JWT token based on authenticated user details
        final String jwtToken = jwtUtil.generateToken(userDetails);

        // Fetch the user's role to include in the response
        String role = userService.getUserRole(request.getEmail());

        // Return email, token, and role to the client
        return new AuthResponse(request.getEmail(), jwtToken, role);
    }

//    Authenticates the user using the authentication manager.
    private void authenticate(String email, String password) throws Exception {
        try {
            // Tries to authenticate with provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (DisabledException e) {
            // If the user account is disabled
            throw new Exception("User disabled");
        } catch (BadCredentialsException e) {
            // If credentials are invalid
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email or password is incorrect"
            );
        }
    }

    /**
     * Exposes an endpoint to encode a raw password.
     * Useful for creating encoded passwords during development or registration.
     */
    @PostMapping("/encode")
    public String encodePassword(@RequestBody Map<String, String> request){
        // Encodes and returns the password string
        return passwordEncoder.encode(request.get("password"));
    }
}
