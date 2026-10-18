package com.astronity.pos.order.dto;

import com.astronity.pos.order.enums.OrderType;
import jakarta.validation.constraints.NotNull;

/** tableId is required for dine_in only; the service checks that rule. */
public record CreateOrderRequest(
        @NotNull OrderType orderType,
        Long tableId) {
}
