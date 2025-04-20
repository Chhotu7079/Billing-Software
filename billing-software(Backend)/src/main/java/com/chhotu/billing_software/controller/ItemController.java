package com.chhotu.billing_software.controller;


import com.chhotu.billing_software.io.ItemRequest;
import com.chhotu.billing_software.io.ItemResponse;
import com.chhotu.billing_software.service.ItemService;
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
public class ItemController {

    private final ItemService itemService; // Injected service to handle item logic (add, fetch, delete)

    /**
     * Endpoint to add a new item.
     * Expects multipart/form-data containing:
     * - item: JSON string representing the item data.
     * - file: An image or file associated with the item.
     * URL: POST /admin/items
     */
    @PostMapping("/admin/items")
    @ResponseStatus(HttpStatus.CREATED) // Return 201 status when item is successfully created
    public ItemResponse addItem(@RequestPart("item") String itemString,
                                @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper(); // Used to parse the JSON string into an object
        ItemRequest itemRequest = null;
        try {
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class); // Convert JSON string to ItemRequest object
            return itemService.add(itemRequest, file); // Call service to add the item
        } catch (JsonProcessingException e) {
            // If JSON parsing fails, throw a 400 Bad Request error with a message
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred while processing the json: " + e.getMessage());
        }
    }

    /**
     * Endpoint to get the list of all items.
     * URL: GET /items
     * Returns: List of item response DTOs
     */
    @GetMapping("/items")
    public List<ItemResponse> readItems() {
        return itemService.fetchItems(); // Call service to get all items
    }

    /**
     * Endpoint to delete an item by its ID.
     * URL: DELETE /admin/items/{itemId}
     * Returns: 204 No Content if successful
     */
    @ResponseStatus(HttpStatus.NO_CONTENT) // Return 204 status on successful deletion
    @DeleteMapping("/admin/items/{itemId}")
    public void removeItem(@PathVariable String itemId) {
        try {
            itemService.deleteItem(itemId); // Attempt to delete the item by ID
        } catch (Exception e) {
            // If item not found or deletion fails, return 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found: " + e.getMessage());
        }
    }

}
