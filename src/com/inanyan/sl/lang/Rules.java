package com.inanyan.sl.lang;

public class Rules {
    public static String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Integer) {
            return intToString((int) object);
        } else {
            throw new RuntimeException("Unimplemented object type stringify rule");
        }
    }

    public static String intToString(int num) {
        return String.valueOf(num);
    }
}
