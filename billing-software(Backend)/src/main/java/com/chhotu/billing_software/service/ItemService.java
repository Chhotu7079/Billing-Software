package com.chhotu.billing_software.service;

import com.chhotu.billing_software.io.ItemRequest;
import com.chhotu.billing_software.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse add(ItemRequest request, MultipartFile file);

    List<ItemResponse> fetchItems();

    void deleteItem(String itemId);
}
