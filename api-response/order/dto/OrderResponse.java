package com.astronity.pos.order.dto;

import com.astronity.pos.order.enums.OrderStatus;
import com.astronity.pos.order.enums.OrderType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderType orderType,
        OrderStatus status,
        String tableName,
        List<OrderItemResponse> items,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        OffsetDateTime openedAt) {
}
