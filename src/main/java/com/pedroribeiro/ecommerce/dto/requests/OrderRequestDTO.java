package com.pedroribeiro.ecommerce.dto.requests;

import java.util.List;
import java.util.UUID;

import com.pedroribeiro.ecommerce.model.enums.OrderStatus;

public class OrderRequestDTO {

    private UUID userId;
    private OrderStatus status;
    private Double totalPrice;
    private List<OrderItemRequestDTO> items;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

}
