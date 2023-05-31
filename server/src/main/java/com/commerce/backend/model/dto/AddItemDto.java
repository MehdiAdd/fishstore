package com.commerce.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AddItemDto {

    private String name;
    private String category;
    private String description;
    private Double price;
    private Double cargo_price;
    private Double tax_percent;
    private Double stock;
    private String  image;
    private String thumb;
}
