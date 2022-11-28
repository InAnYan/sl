package com.inanyan.sl.parsing;

import com.inanyan.sl.util.ErrorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final ErrorListener errorListener;
    private int start, current;
    private int line = 0;

    public Lexer(ErrorListener errorListener, String source) {
        this.errorListener = errorListener;
        this.source = source;
        this.start = 0;
        this.current = 0;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(source.length(), TokenType.EOF, ""));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char cur = advance();
        switch (cur) {
            case ';': addToken(TokenType.SEMICOLON); break;
            case '\n':
                line++;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '#': comment(); break;
            default:
                if (Rules.isDigit(cur)) {
                    number();
                } else if (Rules.isAlphabetic(cur) || cur == '_') {
                    identifierOrKeyword();
                } else {
                    errorListener.reportError(line, "unknown character");
                }
                break;
        }
    }

    private void identifierOrKeyword() {
        while (Rules.isAlphabetic(peek()) || Rules.isDigit(peek()) || peek() == '_') {
            advance();
        }

        String text = source.substring(start, current);
        tokens.add(new Token(line, isKeyword(text), text));
    }

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("print", TokenType.PRINT);
    }

    private TokenType isKeyword(String text) {
        TokenType type = keywords.get(text);
        if (type == null) {
            return TokenType.IDENTIFIER;
        } else {
            return type;
        }
    }

    private void number() {
        while (Rules.isDigit(peek())) {
            advance();
        }
        addToken(TokenType.INT_NUMBER);
    }

    private void comment() {
        while(!isAtEnd() && peek() != '\n') {
            advance();
        }

        if (!isAtEnd()) {
            line++;
            advance();
        }
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(line, type, source.substring(start, current)));
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        else return source.charAt(current);
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }
}
