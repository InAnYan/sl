package com.inanyan.sl.lang;

public class Rules {
    public static String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Integer num) {
            return intToString(num);
        } else if (object instanceof Character ch) {
            return String.valueOf(ch);
        } else if (object instanceof Boolean bool) {
            return bool ? "true" : "false";
        } else if (object instanceof String str) {
            return str;
        } else {
            throw new RuntimeException("Unimplemented object type stringify rule");
        }
    }

    public static String intToString(int num) {
        return String.valueOf(num);
    }
}
