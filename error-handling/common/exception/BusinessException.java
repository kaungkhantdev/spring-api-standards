package com.astronity.pos.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for every expected error in the app: a business rule says no.
 * Unchecked, so services don't need {@code throws} and {@code @Transactional} rolls back.
 */
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    protected BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
