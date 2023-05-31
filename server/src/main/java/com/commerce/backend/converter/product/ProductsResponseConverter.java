package com.commerce.backend.converter.product;

import com.commerce.backend.model.dto.CartItemDTO;
import com.commerce.backend.model.dto.ColorDTO;
import com.commerce.backend.model.dto.ProductVariantDTO;
import com.commerce.backend.model.entity.Cart;
import com.commerce.backend.model.entity.ProductVariant;
import com.commerce.backend.model.response.cart.CartResponse;
import com.commerce.backend.model.response.product.ProductVariantResponse;
import com.commerce.backend.model.response.product.ProductsResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;



@Component
public class ProductsResponseConverter implements Function<List<ProductVariant>, ProductsResponse> {

    @Override
    public ProductsResponse apply(List<ProductVariant> products) {
        ProductsResponse productsResponse = new ProductsResponse();

        productsResponse.setProductItems(products
                .stream()
                .map(cartItem -> ProductVariantResponse
                        .builder()
                        .id(cartItem.getId())
                        .url(cartItem.getProduct().getUrl())
                        .name(cartItem.getProduct().getName())
                        .productVariant(ProductVariantDTO.builder().stock(cartItem.getStock()).thumb(cartItem.getThumb()).price(price(cartItem)).build())
                    .build())

                .collect(Collectors.toList()));



/*        if (Objects.nonNull(cart.getDiscount())) {
            cartResponse.setDiscount(DiscountDTO
                    .builder()
                    .discountPercent(cart.getDiscount().getDiscountPercent())
                    .status(cart.getDiscount().getStatus())
                    .build()
            );
        }*/


        return productsResponse;
    }

    private float price(ProductVariant p){
        return p.getPrice() - (p.getPrice() * (Period.between(p.getDate().toLocalDate(), LocalDate.now()).getDays())/100);
    }
}