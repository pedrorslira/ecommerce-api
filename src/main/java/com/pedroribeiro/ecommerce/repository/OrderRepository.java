package com.pedroribeiro.ecommerce.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pedroribeiro.ecommerce.dto.projections.AverageTicketProjection;
import com.pedroribeiro.ecommerce.dto.projections.TopBuyerProjection;
import com.pedroribeiro.ecommerce.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserUsername(String username);

    @Query(value = """
        SELECT u.username AS userName,
               COUNT(o.id) AS totalOrders,
               SUM(o.total_price) AS totalSpent
        FROM orders o
        JOIN users u ON u.id = o.user_id
        GROUP BY u.id, u.username
        ORDER BY totalSpent DESC
        LIMIT 5
        """, nativeQuery = true)
    List<TopBuyerProjection> findTopBuyers();

    @Query(value = """
    SELECT u.username AS userName,
           COUNT(o.id) AS totalOrders,
           ROUND(SUM(o.total_price) / COUNT(o.id), 2) AS averageTicket
    FROM orders o
    JOIN users u ON u.id = o.user_id
    GROUP BY u.username
    """, nativeQuery = true)
    List<AverageTicketProjection> findAverageTicketPerUser();

    @Query(value = """
        SELECT COALESCE(SUM(o.total_price), 0) AS totalRevenue
        FROM orders o
        WHERE MONTH(o.order_date) = MONTH(CURRENT_DATE())
          AND YEAR(o.order_date) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    BigDecimal findTotalRevenueCurrentMonth();
}
