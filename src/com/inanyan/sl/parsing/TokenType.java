package com.inanyan.sl.parsing;

public enum TokenType {
    // One-char length.
    SEMICOLON,

    // Big.
    IDENTIFIER,
    INT_NUMBER, FLOAT_NUMBER,
    CHARACTER, STRING,

    // Keywords.
    PRINT,
    NIL, TRUE, FALSE,

    // Special.
    EOF
}
