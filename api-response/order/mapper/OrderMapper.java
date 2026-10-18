package com.astronity.pos.order.mapper;

import com.astronity.pos.order.dto.OrderItemResponse;
import com.astronity.pos.order.dto.OrderResponse;
import com.astronity.pos.order.entity.Order;
import com.astronity.pos.order.entity.OrderItem;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/** Copies an Order entity into the OrderResponse the API sends. */
@Component
public class OrderMapper {

    // Order times are stored as the store's local time; all stores are in Thailand
    private static final ZoneId STORE_ZONE = ZoneId.of("Asia/Bangkok");

    /** tableNames comes from the table feature, because Order only stores table ids. */
    public OrderResponse toResponse(Order order, List<String> tableNames) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getOrderType(),
                order.getStatus(),
                tableNames.isEmpty() ? null : String.join(", ", tableNames),
                order.getItems().stream().map(this::toItemResponse).toList(),
                order.getSubtotal(),
                order.getTaxAmount(),
                order.getTotal(),
                toStoreTime(order));
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductName(),
                item.getQty(),
                item.getUnitPrice(),
                item.getLineTotal());
    }

    private static OffsetDateTime toStoreTime(Order order) {
        return order.getOpenedAt() == null ? null : order.getOpenedAt().atZone(STORE_ZONE).toOffsetDateTime();
    }
}
