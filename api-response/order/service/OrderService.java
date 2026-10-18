package com.astronity.pos.order.service;

import com.astronity.pos.order.dto.CreateOrderRequest;
import com.astronity.pos.order.dto.OrderResponse;
import com.astronity.pos.order.enums.OrderStatus;
import com.astronity.pos.order.exception.OrderNotFoundException;
import com.astronity.pos.order.exception.OrderStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** What the controller needs from orders. Returns DTOs, never entities. */
public interface OrderService {

    /** @throws OrderNotFoundException if the order doesn't exist */
    OrderResponse getOrder(Long id);

    /** status may be null for all orders. */
    Page<OrderResponse> listOrders(OrderStatus status, Pageable pageable);

    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * @throws OrderNotFoundException if the order doesn't exist
     * @throws OrderStateException    if the order is already paid
     */
    OrderResponse voidOrder(Long id);

    /** @throws OrderNotFoundException if the order or the item doesn't exist */
    void removeItem(Long orderId, Long itemId);
}
