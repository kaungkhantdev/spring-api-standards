package com.astronity.pos.common.response;

import org.springframework.data.domain.Page;

/** Where a page sits in the whole list. page starts at 0. */
public record Pagination(int page, int size, long totalElements, int totalPages) {

    public static Pagination of(Page<?> page) {
        return new Pagination(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
