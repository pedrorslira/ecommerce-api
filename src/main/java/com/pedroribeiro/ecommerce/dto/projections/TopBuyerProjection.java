package com.pedroribeiro.ecommerce.dto.projections;

import java.math.BigDecimal;

public interface TopBuyerProjection {
    String getUserName();
    Long getTotalOrders();
    BigDecimal getTotalSpent();
}