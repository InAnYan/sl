package com.inanyan.sl.parsing;

public enum TokenType {
    // One-char length.
    SEMICOLON,

    // Big.
    INT_NUMBER,
    IDENTIFIER,
    CHARACTER,
    STRING,

    // Keywords.
    PRINT,
    NIL, TRUE, FALSE,

    // Special.
    EOF
}
