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
            case '\'': character(); break;
            case '\"': string(); break;
            default:
                if (Rules.isDigit(cur)) {
                    number();
                } else if (Rules.isAlphabetic(cur) || cur == '_') {
                    identifierOrKeyword();
                } else {
                    errorListener.reportError(line, "unexpected character");
                }
                break;
        }
    }

    private char scanStringChar() {
        // TODO: What to do with literal new line in char or string?
        char ch = advance();
        if (ch == '\\') {
            return escape(advance());
        } else {
            return ch;
        }
    }

    private char escape(char ch) {
        switch (ch) {
            case 't':  return '\t';
            case 'n':  return '\n';
            case 'r':  return '\r';

            case '\\':
            case '\'':
            case '\"':
                return ch;

            default:
                errorListener.reportWarning(line, "unknown escape sequence, leaving as is");
                return ch;
        }
    }

    private void string() {
        StringBuilder sb = new StringBuilder();
        while (!isAtEnd() && peek() != '\"') {
            sb.append(scanStringChar());
        }

        if (peek() != '\"') {
            errorListener.reportError(line, "unterminated string");
        } else {
            advance();
        }

        addToken(TokenType.STRING, sb.toString());
    }

    // TODO: Better. Make accepting as string, but to be error.
    private void character() {
        char ch = scanStringChar();

        if (peek() != '\'') {
            errorListener.reportError(line, "unterminated character literal");
        } else {
            advance();
        }

        addToken(TokenType.CHARACTER, String.valueOf(ch));
    }

    private void identifierOrKeyword() {
        while (Rules.isAlphaDigit(peek()) || peek() == '_') {
            advance();
        }

        String text = source.substring(start, current);
        tokens.add(new Token(line, isKeyword(text), text));
    }

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("print", TokenType.PRINT);
        keywords.put("nil", TokenType.NIL);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
    }

    private TokenType isKeyword(String text) {
        TokenType type = keywords.get(text);
        if (type == null) {
            return TokenType.IDENTIFIER;
        } else {
            return type;
        }
    }

    private void collectNumbers() {
        while (Rules.isDigit(peek())) {
            advance();
        }
    }

    private void number() {
        collectNumbers();

        boolean isFloat = false;
        if (peek() == '.') {
            advance();
            collectNumbers();
            addToken(TokenType.FLOAT_NUMBER);
        } else {
            addToken(TokenType.INT_NUMBER);
        }
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

    private void addToken(TokenType type, String str) {
        tokens.add(new Token(line, type, str));
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
