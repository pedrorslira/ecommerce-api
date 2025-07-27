package com.pedroribeiro.ecommerce.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pedroribeiro.ecommerce.dto.OrderDTO;
import com.pedroribeiro.ecommerce.security.JwtService;
import com.pedroribeiro.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO request, @RequestParam String username) {
        OrderDTO orderDTO = orderService.createOrder(request, username);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable UUID id, @RequestParam String username) {
        OrderDTO orderDTO = orderService.payOrder(id, username);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        String username = jwtService.extractUsername(token);
        return ResponseEntity.ok(orderService.getUserOrders(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-buyers")
    public ResponseEntity<?> getTopBuyers() {
        return ResponseEntity.ok(orderService.getTopBuyers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/avg-ticket")
    public ResponseEntity<?> getAverageTicket() {
        return ResponseEntity.ok(orderService.getAverageTicketPerUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/total-monthly")
    public ResponseEntity<?> getMonthlyRevenue() {
        return ResponseEntity.ok(orderService.getTotalRevenueCurrentMonth());
    }
}
