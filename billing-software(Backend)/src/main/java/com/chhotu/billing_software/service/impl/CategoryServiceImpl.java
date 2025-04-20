package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.entity.CategoryEntity;
import com.chhotu.billing_software.io.CategoryRequest;
import com.chhotu.billing_software.io.CategoryResponse;
import com.chhotu.billing_software.repository.CategoryRepository;
import com.chhotu.billing_software.repository.ItemRepository;
import com.chhotu.billing_software.service.CategoryService;
import com.chhotu.billing_software.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    // Injecting dependencies using constructor injection
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;

    /**
     * Adds a new category with the provided data and file (image).
     * @param request The category details.
     * @param file The image file.
     * @return CategoryResponse object with saved category details.
     */
    @Override
    public CategoryResponse add(CategoryRequest request, MultipartFile file) {
        // Upload the file and get the image URL
        String imgUrl = fileUploadService.uploadFile(file);

        // Convert request to entity and set the uploaded image URL
        CategoryEntity newCategory = convertToEntity(request);
        newCategory.setImgUrl(imgUrl);

        // Save the category to the database
        newCategory = categoryRepository.save(newCategory);

        // Convert saved entity to response and return
        return convertToResponse(newCategory);
    }

    /**
     * Reads all categories and returns them as a list of responses.
     * @return List of CategoryResponse
     */
    @Override
    public List<CategoryResponse> read() {
        // Fetch all categories and map them to response objects
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a category by its categoryId and also deletes its image file.
     * @param categoryId Unique category identifier
     */
    @Override
    public void delete(String categoryId) {
        // Find the category by ID or throw an exception if not found
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: "+ categoryId));

        // Delete the associated image file
        fileUploadService.deleteFile(existingCategory.getImgUrl());

        // Delete the category from the database
        categoryRepository.delete(existingCategory);
    }

    /**
     * Converts a CategoryEntity to CategoryResponse, including item count.
     * @param newCategory The saved CategoryEntity
     * @return CategoryResponse object
     */
    private CategoryResponse convertToResponse(CategoryEntity newCategory) {
        // Count the items associated with the category
        Integer itemsCount = itemRepository.countByCategoryId(newCategory.getId());

        // Build and return the response object
        return CategoryResponse.builder()
                .categoryId(newCategory.getCategoryId())
                .name(newCategory.getName())
                .description(newCategory.getDescription())
                .bgColor(newCategory.getBgColor())
                .imgUrl(newCategory.getImgUrl())
                .createdAt(newCategory.getCreatedAt())
                .updatedAt(newCategory.getUpdatedAt())
                .items(itemsCount)
                .build();
    }

    /**
     * Converts CategoryRequest to CategoryEntity.
     * @param request The incoming category request.
     * @return CategoryEntity ready for persistence.
     */
    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString()) // Generate a unique ID for the category
                .name(request.getName())
                .description(request.getDescription())
                .bgColor(request.getBgColor())
                .build();
    }
}
