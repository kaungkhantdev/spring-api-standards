package com.astronity.pos.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}
