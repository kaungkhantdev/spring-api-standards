package com.astronity.pos.common.response;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Every response the API sends, success or error, has these four fields.
 * Controllers return ApiResponse.ok(...); GlobalExceptionHandler returns ApiResponse.fail(...).
 */
public record ApiResponse<T>(boolean success, T data, ApiError error, Meta meta) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, Meta.now());
    }

    /** Success with nothing to return, e.g. after a delete. */
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null, Meta.now());
    }

    /** A list: the items go in data, the page numbers in meta.pagination. */
    public static <T> ApiResponse<List<T>> ok(Page<T> page) {
        return new ApiResponse<>(true, page.getContent(), null, Meta.now(Pagination.of(page)));
    }

    public static ApiResponse<Void> fail(ApiError error) {
        return new ApiResponse<>(false, null, error, Meta.now());
    }
}
