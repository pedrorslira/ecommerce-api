package com.pedroribeiro.ecommerce.dto.requests;

import java.util.List;

public class OrderRequestDTO {
    private List<OrderItemRequestDTO> items;

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

}
