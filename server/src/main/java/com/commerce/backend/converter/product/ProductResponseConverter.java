package com.commerce.backend.converter.product;

import com.commerce.backend.model.dto.ColorDTO;
import com.commerce.backend.model.dto.ProductVariantDTO;
import com.commerce.backend.model.entity.Product;
import com.commerce.backend.model.entity.ProductVariant;
import com.commerce.backend.model.response.product.ProductResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class ProductResponseConverter implements Function<Product, ProductResponse> {

    @Override
    public ProductResponse apply(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName(product.getName());
        productResponse.setUrl(product.getUrl());
        productResponse.setProductVariants(product.getProductVariantList()
                .stream()
                .map(variant -> ProductVariantDTO
                        .builder()
                        .id(variant.getId())
                        .price(price(variant))
                        .oldPrice(variant.getPrice())
                        .thumb(variant.getThumb())
                        .stock(variant.getStock())
                        .build())
                .collect(Collectors.toList()));

        return productResponse;
    }
    private float price(ProductVariant p){
        return p.getPrice() - (p.getPrice() * (Period.between(p.getDate().toLocalDate(), LocalDate.now()).getDays())/100);
    }
}
