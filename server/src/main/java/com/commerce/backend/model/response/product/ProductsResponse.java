package com.commerce.backend.model.response.product;


import lombok.Data;

import java.util.List;



@Data
public class ProductsResponse {
    private List<ProductVariantResponse> productItems;
//    private DiscountDTO discount;
//    private Float totalCartPrice;
//    private Float totalCargoPrice;
//    private Float totalPrice;
}