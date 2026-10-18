package com.astronity.pos.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/** When the response was made; pagination only on lists. */
public record Meta(
        OffsetDateTime timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) Pagination pagination) {

    public static Meta now() {
        return now(null);
    }

    public static Meta now(Pagination pagination) {
        return new Meta(OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS), pagination);
    }
}
