package com.chhotu.billing_software.io;

import lombok.Builder;
import lombok.Data;


import java.sql.Timestamp;

@Data
@Builder
public class CategoryResponse {

    private String categoryId;
    private String name;
    private String description;
    private String bgColor;
    private String imgUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer items;
}
