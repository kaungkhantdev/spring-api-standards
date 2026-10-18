package com.astronity.pos.common.response;

import java.util.List;

/** What went wrong. details lists the invalid fields on a validation error, and is empty otherwise. */
public record ApiError(String code, String message, List<FieldError> details) {

    public record FieldError(String field, String message) {
    }

    public static ApiError of(String code, String message) {
        return new ApiError(code, message, List.of());
    }
}
