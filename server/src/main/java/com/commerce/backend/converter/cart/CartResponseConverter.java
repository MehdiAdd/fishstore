package com.commerce.backend.converter.cart;

import com.commerce.backend.model.dto.CartItemDTO;
import com.commerce.backend.model.dto.ColorDTO;
import com.commerce.backend.model.dto.DiscountDTO;
import com.commerce.backend.model.entity.Cart;
import com.commerce.backend.model.entity.ProductVariant;
import com.commerce.backend.model.response.cart.CartResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CartResponseConverter implements Function<Cart, CartResponse> {

    @Override
    public CartResponse apply(Cart cart) {
        CartResponse cartResponse = new CartResponse();

        cartResponse.setCartItems(cart.getCartItemList()
                .stream()
                .map(cartItem -> CartItemDTO
                        .builder()
                        .id(cartItem.getId())
                        .url(cartItem.getProductVariant().getProduct().getUrl())
                        .name(cartItem.getProductVariant().getProduct().getName())
                        .price( price(cartItem.getProductVariant()))
                        .amount(cartItem.getAmount())
                        .thumb(cartItem.getProductVariant().getThumb())
                        .stock(cartItem.getProductVariant().getStock())
                        .color(ColorDTO.builder()
                                .build())
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

        cartResponse.setTotalCartPrice(cart.getTotalCartPrice());
        cartResponse.setTotalCargoPrice(cart.getTotalCargoPrice());
        cartResponse.setTotalPrice(cart.getTotalPrice());
        return cartResponse;
    }

    private float price(ProductVariant p){
        return p.getPrice() - (p.getPrice() * (Period.between(p.getDate().toLocalDate(), LocalDate.now()).getDays())/100);
    }
}
