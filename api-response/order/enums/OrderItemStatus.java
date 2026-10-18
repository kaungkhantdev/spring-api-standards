package com.astronity.pos.order.enums;

import com.astronity.pos.common.enums.DbEnum;
import com.astronity.pos.common.enums.DbEnumConverter;
import jakarta.persistence.Converter;

public enum OrderItemStatus implements DbEnum {
    PENDING("pending"),
    SENT("sent"),
    VOID("void");

    private final String value;

    OrderItemStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    // Entity field <-> column: stores the lowercase value, no @Enumerated needed
    @Converter(autoApply = true)
    public static class JpaConverter extends DbEnumConverter<OrderItemStatus> {
        public JpaConverter() {
            super(OrderItemStatus.class);
        }
    }
}
