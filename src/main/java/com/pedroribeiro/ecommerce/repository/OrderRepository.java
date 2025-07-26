package com.pedroribeiro.ecommerce.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pedroribeiro.ecommerce.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}