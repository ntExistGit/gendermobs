package com.ntexist.mcidentitymobs.enums;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String s) {
        if (s == null) return null;
        return switch (s.toLowerCase()) {
            case "male" -> MALE;
            case "female" -> FEMALE;
            default -> null;
        };
    }
}
