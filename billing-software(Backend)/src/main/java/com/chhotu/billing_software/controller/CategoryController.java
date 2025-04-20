package com.chhotu.billing_software.controller;

import com.chhotu.billing_software.io.CategoryRequest;
import com.chhotu.billing_software.io.CategoryResponse;
import com.chhotu.billing_software.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService; // Service layer to handle business logic

    /**
     * Adds a new category (admin only).
     * Accepts multipart/form-data with category JSON and an image file.
     */
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created on success
    public CategoryResponse addCategory(
            @RequestPart("category") String categoryString, // JSON string representing the category
            @RequestPart("file") MultipartFile file // Image or any file associated with the category
    ) {
        ObjectMapper objectMapper = new ObjectMapper(); // Used to convert JSON string to Java object
        CategoryRequest request = null;

        try {
            // Deserialize JSON string into CategoryRequest object
            request = objectMapper.readValue(categoryString, CategoryRequest.class);

            // Delegate the addition of the category to the service layer
            return categoryService.add(request, file);

        } catch (JsonProcessingException ex) {
            // If JSON parsing fails, return 400 Bad Request
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Exception Occurred while parsing the JSON: " + ex.getMessage()
            );
        }
    }

    /**
     * Fetches all categories (accessible to all authenticated users).
     */
    @GetMapping("/categories")
    public List<CategoryResponse> fetchCategories() {
        return categoryService.read(); // Delegates fetching logic to the service
    }

    /**
     * Deletes a category by ID (admin only).
     */
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns 204 No Content if deletion is successful
    @DeleteMapping("/admin/categories/{categoryId}")
    public void remove(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId); // Delegate deletion to service
        } catch (Exception e) {
            // If category not found or deletion fails, return 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}