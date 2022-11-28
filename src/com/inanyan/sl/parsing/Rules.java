package com.inanyan.sl.parsing;

public class Rules {
    public static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean isAlphabetic(char ch) {
        return Character.isAlphabetic(ch);
    }

    public static boolean isAlphaDigit(char ch) {
        return isDigit(ch) || isAlphabetic(ch);
    }
}
