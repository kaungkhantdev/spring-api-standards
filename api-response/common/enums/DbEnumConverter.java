package com.astronity.pos.common.enums;

import jakarta.persistence.AttributeConverter;

/** Stores an enum as its lowercase value (dine_in) and reads it back. Each enum has a JpaConverter that extends this. */
public abstract class DbEnumConverter<E extends Enum<E> & DbEnum> implements AttributeConverter<E, String> {

    private final Class<E> type;

    protected DbEnumConverter(Class<E> type) {
        this.type = type;
    }

    @Override
    public String convertToDatabaseColumn(E value) {
        return value == null ? null : value.getValue();
    }

    @Override
    public E convertToEntityAttribute(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.getValue().equals(dbValue)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Unknown " + type.getSimpleName() + " value '" + dbValue + "'");
    }
}
