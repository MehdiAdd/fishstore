package com.commerce.backend.converter.order;

import com.commerce.backend.model.dto.CategoryDTO;
import com.commerce.backend.model.dto.ColorDTO;
import com.commerce.backend.model.dto.DiscountDTO;
import com.commerce.backend.model.dto.OrderDetailDTO;
import com.commerce.backend.model.entity.Order;
import com.commerce.backend.model.entity.ProductVariant;
import com.commerce.backend.model.response.order.OrderResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderResponseConverter implements Function<Order, OrderResponse> {
    @Override
    public OrderResponse apply(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setShipName(order.getShipName());
        orderResponse.setShipAddress(order.getShipAddress());
        orderResponse.setBillingAddress(order.getBillingAddress());
        orderResponse.setCity(order.getCity());
        orderResponse.setCountry(order.getCountry());
        orderResponse.setState(order.getState());
        orderResponse.setZip(order.getZip());
        orderResponse.setPhone(order.getPhone());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setTotalCargoPrice(order.getTotalCargoPrice());
        orderResponse.setDate(order.getDate().getTime());
        orderResponse.setShipped(order.getShipped());
        orderResponse.setCargoFirm(order.getCargoFirm());
        orderResponse.setTrackingNumber(order.getTrackingNumber());


        orderResponse.setOrderItems(
                order.getOrderDetailList()
                        .stream()
                        .map(orderDetails -> OrderDetailDTO
                                .builder()
                                .url(orderDetails.getProductVariant().getProduct().getUrl())
                                .name(orderDetails.getProductVariant().getProduct().getName())
                                .price(price(orderDetails.getProductVariant()))
                                .cargoPrice(orderDetails.getProductVariant().getCargoPrice())
                                .thumb(orderDetails.getProductVariant().getThumb())
                                .amount(orderDetails.getAmount())
                                .category(CategoryDTO
                                        .builder()
                                        .name(orderDetails.getProductVariant().getProduct().getProductCategory().getName())
                                        .build())
                                .build()
                        ).collect(Collectors.toList())
        );

        return orderResponse;
    }

    private float price(ProductVariant p){
        return p.getPrice() - (p.getPrice() * (Period.between(p.getDate().toLocalDate(), LocalDate.now()).getDays())/100);
    }
}
