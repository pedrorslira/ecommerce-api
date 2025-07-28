package com.pedroribeiro.ecommerce.dto.projections;

import java.math.BigDecimal;

public interface AverageTicketProjection {
    String getUserName();
    Long getTotalOrders();
    BigDecimal getAverageTicket();
}
