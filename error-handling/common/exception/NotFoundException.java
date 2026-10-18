package com.astronity.pos.common.exception;

import org.springframework.http.HttpStatus;

/** Base class for "the record doesn't exist": always 404. */
public abstract class NotFoundException extends BusinessException {

    protected NotFoundException(String code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
