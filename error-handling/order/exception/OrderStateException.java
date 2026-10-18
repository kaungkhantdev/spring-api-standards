package com.astronity.pos.order.exception;

import com.astronity.pos.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

/** The order's status doesn't allow this action, e.g. voiding a paid order. */
public class OrderStateException extends BusinessException {

    public OrderStateException(String message) {
        super(HttpStatus.CONFLICT, "ORDER_STATE_INVALID", message);
    }
}
