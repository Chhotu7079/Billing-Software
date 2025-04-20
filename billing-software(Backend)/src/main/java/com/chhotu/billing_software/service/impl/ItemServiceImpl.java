package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.entity.CategoryEntity;
import com.chhotu.billing_software.entity.ItemEntity;
import com.chhotu.billing_software.io.ItemRequest;
import com.chhotu.billing_software.io.ItemResponse;
import com.chhotu.billing_software.repository.CategoryRepository;
import com.chhotu.billing_software.repository.ItemRepository;
import com.chhotu.billing_software.service.FileUploadService;
import com.chhotu.billing_software.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    // Inject the FileUploadService for handling file uploads (e.g., images)
    private final FileUploadService fileUploadService;

    // Inject the CategoryRepository for interacting with Category entities
    private final CategoryRepository categoryRepository;

    // Inject the ItemRepository for interacting with Item entities
    private final ItemRepository itemRepository;

    /**
     * Adds a new item to the system. The item is associated with a category and a file (image) is uploaded.
     * @param request The item details from the user.
     * @param file The file to be uploaded (image of the item).
     * @return The response object containing the item details.
     */
    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) {
        // Upload the file and get the file's URL
        String imgUrl = fileUploadService.uploadFile(file);

        // Convert the ItemRequest to an ItemEntity
        ItemEntity newItem = convertToEntity(request);

        // Find the category that the item belongs to
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: " + request.getCategoryId()));

        // Set the category and image URL for the new item
        newItem.setCategory(existingCategory);
        newItem.setImgUrl(imgUrl);

        // Save the new item to the database
        newItem = itemRepository.save(newItem);

        // Convert the saved item to a response object and return it
        return convertToResponse(newItem);
    }

    /**
     * Converts an ItemEntity object to an ItemResponse object.
     * @param newItem The ItemEntity to be converted.
     * @return The converted ItemResponse object.
     */
    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .imgUrl(newItem.getImgUrl())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    /**
     * Converts an ItemRequest object to an ItemEntity object.
     * @param request The ItemRequest to be converted.
     * @return The converted ItemEntity object.
     */
    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString()) // Generate a unique ID for the item
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    /**
     * Fetches all items from the database.
     * @return A list of ItemResponse objects representing all items.
     */
    @Override
    public List<ItemResponse> fetchItems() {
        // Fetch all items from the repository, convert each item to a response, and return the list
        return itemRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes an item from the system by its ID.
     * @param itemId The ID of the item to be deleted.
     */
    @Override
    public void deleteItem(String itemId) {
        // Find the item by its ID
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        // Delete the file associated with the item (e.g., image) from S3
        boolean isFileDelete = fileUploadService.deleteFile(existingItem.getImgUrl());

        // If the file was successfully deleted, delete the item from the database
        if (isFileDelete) {
            itemRepository.delete(existingItem);
        } else {
            // If the file couldn't be deleted, throw an exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete the image");
        }
    }
}
