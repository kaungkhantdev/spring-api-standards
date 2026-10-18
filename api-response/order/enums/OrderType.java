package com.astronity.pos.order.enums;

import com.astronity.pos.common.enums.DbEnum;
import com.astronity.pos.common.enums.DbEnumConverter;
import jakarta.persistence.Converter;

public enum OrderType implements DbEnum {
    DINE_IN("dine_in"),
    TAKEAWAY("takeaway"),
    DELIVERY("delivery");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    // Entity field <-> column: stores the lowercase value, no @Enumerated needed
    @Converter(autoApply = true)
    public static class JpaConverter extends DbEnumConverter<OrderType> {
        public JpaConverter() {
            super(OrderType.class);
        }
    }
}
