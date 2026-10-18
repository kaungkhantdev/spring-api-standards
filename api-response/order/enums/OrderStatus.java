package com.astronity.pos.order.enums;

import com.astronity.pos.common.enums.DbEnum;
import com.astronity.pos.common.enums.DbEnumConverter;
import jakarta.persistence.Converter;

public enum OrderStatus implements DbEnum {
    OPEN("open"),
    SERVED("served"),
    PAID("paid"),
    COMPLETED("completed"),
    VOID("void");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    // Entity field <-> column: stores the lowercase value, no @Enumerated needed
    @Converter(autoApply = true)
    public static class JpaConverter extends DbEnumConverter<OrderStatus> {
        public JpaConverter() {
            super(OrderStatus.class);
        }
    }
}
