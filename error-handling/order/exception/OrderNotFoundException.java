package com.astronity.pos.order.exception;

import com.astronity.pos.common.exception.NotFoundException;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("ORDER_NOT_FOUND", "Order " + id + " not found");
    }
}
