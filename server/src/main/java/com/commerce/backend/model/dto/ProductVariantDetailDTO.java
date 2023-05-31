package com.commerce.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductVariantDetailDTO {
    private Long id;
    private Float price;
    private Float oldPrice;
    private Float cargoPrice;
    private Float taxPercent;
    private String image;
    private String thumb;
    private Integer stock;
    private Integer live;
    private ColorDTO color;
}