package com.astronity.pos.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * An enum whose value in the database and the API is lowercase (dine_in),
 * while the Java constant is uppercase (DINE_IN).
 */
public interface DbEnum {

    @JsonValue
    String getValue();
}
