package com.pedroribeiro.ecommerce.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedroribeiro.ecommerce.dto.OrderDTO;
import com.pedroribeiro.ecommerce.dto.OrderItemDTO;
import com.pedroribeiro.ecommerce.model.Order;
import com.pedroribeiro.ecommerce.model.OrderItem;
import com.pedroribeiro.ecommerce.model.Product;
import com.pedroribeiro.ecommerce.model.User;
import com.pedroribeiro.ecommerce.model.enums.OrderStatus;
import com.pedroribeiro.ecommerce.repository.OrderRepository;
import com.pedroribeiro.ecommerce.repository.ProductRepository;
import com.pedroribeiro.ecommerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service

public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

        order.setOrderDate(date);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setPrice(product.getPrice());

            items.add(item);
            total += product.getPrice() * itemDTO.getQuantity();
        }

        order.setTotalPrice(total);
        order.setItems(items);
        orderRepository.save(order);

        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO payOrder(UUID orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already paid or cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                order.setStatus(OrderStatus.CANCELED);
                orderRepository.save(order);
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Product out of stock: " + product.getName());
            }
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return mapToDTO(order);
    }

    public List<OrderDTO> getUserOrders(String username) {
        List<Order> orders = orderRepository.findByUserUsername(username);
        return orders.stream().map(this::mapToDTO).toList();
    }

    public List<?> getTopBuyers() {
        return orderRepository.findTopBuyers();
    }

    public List<?> getAverageTicketPerUser() {
        return orderRepository.findAverageTicketPerUser();
    }

    public BigDecimal getTotalRevenueCurrentMonth() {
        return orderRepository.findTotalRevenueCurrentMonth();
    }

    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setPrice(item.getPrice());
            dto.setQuantity(item.getQuantity());
            return dto;
        }).toList();

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setItems(itemDTOs);

        return dto;
    }

}
