package com.chhotu.billing_software.controller;


import com.chhotu.billing_software.io.UserRequest;
import com.chhotu.billing_software.io.UserResponse;
import com.chhotu.billing_software.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserController {

    private final UserService userService;

    /**
     * Endpoint to register a new user.
     * Method: POST
     * URL: /admin/register
     * Request Body: UserRequest (contains user registration details)
     * Response: UserResponse (user info without sensitive data)
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 CREATED on successful registration
    public UserResponse registerUser(@RequestBody UserRequest request){
        try {
            return userService.createUser(request); // Delegate to service layer
        } catch (Exception e) {
            // Throw 400 BAD_REQUEST if registration fails
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create user " + e.getMessage());
        }
    }

    /**
     * Endpoint to get a list of all users.
     * Method: GET
     * URL: /admin/users
     * Response: List of UserResponse
     */
    @GetMapping("/users")
    public List<UserResponse> readUsers(){
        return userService.readUsers(); // Fetch user list
    }

    /**
     * Endpoint to delete a specific user by ID.
     * Method: DELETE
     * URL: /admin/users/{id}
     * Response: 204 NO_CONTENT
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id){
        try {
            userService.deleteUser(id); // Attempt to delete user
        } catch (Exception e) {
            // Throw 404 NOT_FOUND if user doesn't exist
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found " + e.getMessage());
        }
    }
}
