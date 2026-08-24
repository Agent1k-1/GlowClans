package com.agent1k.upgrade;

public enum UpgradeType {

    DEFAULT,
    UPGRADE_CHEST;

    public static UpgradeType fromString(String value) {
        if (value == null) return DEFAULT;
        switch (value.toUpperCase().replace("-", "_")) {
            case "UPGRADE_CHEST": return UPGRADE_CHEST;
            default: return DEFAULT;
        }
    }
}
