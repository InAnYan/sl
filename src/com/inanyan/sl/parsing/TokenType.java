package com.inanyan.sl.parsing;

public enum TokenType {
    // One-char length.
    SEMICOLON,

    // Math
    BANG, MINUS, PLUS,

    // Bitwise operators
    TILDA,

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
