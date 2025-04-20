package com.chhotu.billing_software.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Secret key for signing JWT tokens (loaded from application properties)
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    /**
     * Generates a JWT token for the given UserDetails.
     * @param userDetails The UserDetails object that contains the user information.
     * @return A JWT token as a string.
     */
    public String generateToken(UserDetails userDetails){
        // Creating claims as a map for storing additional token information (if needed)
        Map<String, Object> claims = new HashMap<>();

        // Creating the token with claims and user information (username)
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the specified claims and subject (user information).
     * @param claims The claims (additional information) to be included in the token.
     * @param subject The subject of the token (typically the username).
     * @return The generated JWT token.
     */
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims) // Set custom claims
                .setSubject(subject) // Set subject (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set token issue date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Set token expiration time (10 hours)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign the token with HS256 algorithm and the secret key
                .compact(); // Return the token as a compact string
    }

    /**
     * Extracts the username from the JWT token.
     * @param token The JWT token.
     * @return The username (subject) extracted from the token.
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject); // Extract and return the subject (username)
    }

    /**
     * Extracts the expiration date from the JWT token.
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration); // Extract and return the expiration date
    }

    /**
     * Extracts a specific claim from the JWT token.
     * @param token The JWT token.
     * @param claimsResolver A function to extract a specific claim (e.g., username, expiration).
     * @param <T> The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token); // Extract all claims from the token
        return claimsResolver.apply(claims); // Use the claimsResolver function to extract the specific claim
    }

    /**
     * Extracts all claims from the JWT token.
     * @param token The JWT token.
     * @return The claims extracted from the token.
     */
    private Claims extractAllClaims(String token){
        // Parse the token using the secret key to extract claims
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the JWT token has expired.
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date()); // Check if the expiration date is before the current date
    }

    /**
     * Validates the JWT token by comparing the username and checking if the token is expired.
     * @param token The JWT token.
     * @param userDetails The UserDetails object of the user to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token); // Extract username from token
        // Check if the username matches the userDetails and if the token is not expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
