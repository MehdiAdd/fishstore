package com.commerce.backend.converter.product;

import com.commerce.backend.model.dto.ColorDTO;
import com.commerce.backend.model.dto.ProductVariantDTO;
import com.commerce.backend.model.entity.ProductVariant;
import com.commerce.backend.model.response.product.ProductVariantResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.function.Function;

@Component
public class ProductVariantResponseConverter implements Function<ProductVariant, ProductVariantResponse> {
    @Override
    public ProductVariantResponse apply(ProductVariant productVariant) {
        ProductVariantResponse productVariantResponse = new ProductVariantResponse();
        productVariantResponse.setId(productVariant.getId());
        productVariantResponse.setName(productVariant.getProduct().getName());
        productVariantResponse.setUrl(productVariant.getProduct().getUrl());
        productVariantResponse.setProductVariant(ProductVariantDTO
                .builder()
                .id(productVariant.getId())
                .price(price(productVariant))
                        .oldPrice(productVariant.getPrice())
                .thumb(productVariant.getThumb())
                .stock(productVariant.getStock())
                .build()
        );

        return productVariantResponse;
    }

    private float price(ProductVariant p){
        return p.getPrice() - (p.getPrice() * (Period.between(p.getDate().toLocalDate(), LocalDate.now()).getDays())/100);
    }
}
