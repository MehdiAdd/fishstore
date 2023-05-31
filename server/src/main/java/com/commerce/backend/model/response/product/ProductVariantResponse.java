package com.commerce.backend.model.response.product;

import com.commerce.backend.model.dto.ProductVariantDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponse {
    private Long id;
    private String name;
    private String url;
    private ProductVariantDTO productVariant;
}
