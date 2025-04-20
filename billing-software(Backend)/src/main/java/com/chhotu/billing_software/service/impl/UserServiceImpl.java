package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.entity.UserEntity;
import com.chhotu.billing_software.io.UserRequest;
import com.chhotu.billing_software.io.UserResponse;
import com.chhotu.billing_software.repository.UserRepository;
import com.chhotu.billing_software.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user based on the provided request data.
     * @param request The user data to create the user.
     * @return A UserResponse object containing the created user's details.
     */
    @Override
    public UserResponse createUser(UserRequest request) {
        // Convert the UserRequest to UserEntity and save it to the database
        UserEntity newUser = convertToEntity(request);
        newUser = userRepository.save(newUser);

        // Return the response with the created user details
        return convertToResponse(newUser);
    }

    /**
     * Converts a UserEntity to a UserResponse object.
     * @param newUser The UserEntity object to be converted.
     * @return The UserResponse object containing the user details.
     */
    private UserResponse convertToResponse(UserEntity newUser) {
        return UserResponse.builder()
                .name(newUser.getName()) // User's name
                .email(newUser.getEmail()) // User's email
                .userId(newUser.getUserId()) // User's ID
                .createdAt(newUser.getCreatedAt()) // User's creation timestamp
                .updatedAt(newUser.getUpdatedAt()) // User's last update timestamp
                .role(newUser.getRole()) // User's role
                .build();
    }

    /**
     * Converts a UserRequest object to a UserEntity object.
     * @param request The UserRequest object containing user details.
     * @return A new UserEntity object with the given data.
     */
    private UserEntity convertToEntity(UserRequest request){
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString()) // Generate a unique user ID
                .email(request.getEmail()) // Set the email
                .password(passwordEncoder.encode(request.getPassword())) // Encode the password
                .role(request.getRole().toUpperCase()) // Set the role and convert to uppercase
                .name(request.getName()) // Set the user's name
                .build();
    }

    /**
     * Retrieves the role of a user based on their email address.
     * @param email The email address of the user.
     * @return The role of the user.
     * @throws UsernameNotFoundException If the user with the given email is not found.
     */
    @Override
    public String getUserRole(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the email: " + email));

        return existingUser.getRole();
    }

    /**
     * Retrieves a list of all users in the system.
     * @return A list of UserResponse objects representing all users.
     */
    @Override
    public List<UserResponse> readUsers() {
        // Convert the list of UserEntities to UserResponses and return it
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a user from the system based on the provided user ID.
     * @param id The ID of the user to be deleted.
     * @throws UsernameNotFoundException If the user with the given ID is not found.
     */
    @Override
    public void deleteUser(String id) {
        // Find the user by their userId and delete if found
        UserEntity existingUser = userRepository.findByUserId(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        userRepository.delete(existingUser);
    }
}
