package com.agent1k.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DbType {
    SQLITE,
    MYSQL;

    @NotNull
    public static DbType fromString(@Nullable String value) {
        if (value == null) return SQLITE;
        try {
            return DbType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SQLITE;
        }
    }
}
