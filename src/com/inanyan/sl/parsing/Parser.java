package com.inanyan.sl.parsing;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.util.ErrorListener;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final ErrorListener errorListener;
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(ErrorListener errorListener, List<Token> tokens) {
        this.errorListener = errorListener;
        this.tokens = tokens;
    }

    private static class ParserError extends RuntimeException {}

    public List<Stmt> parse() {
        List<Stmt> result = new ArrayList<>();

        while(!isAtEnd()) {
            skipSemicolons();

            if (isAtEnd()) break;

            try {
                result.add(statement());
            } catch (ParserError e) {
                synchronize();
            }
        }

        return result;
    }

    // TODO: Test somehow
    private void synchronize() {
        if (!isAtEnd()) advance();
        while (true) {
            if (previous().type == TokenType.SEMICOLON) return;
            switch (peek().type) {
                case PRINT, EOF -> {
                    return;
                }
                default -> advance();
            }
        }
    }

    private Stmt statement() {
        if (match(TokenType.PRINT)) return printStmt();
        else return exprStmt();
    }

    private Stmt.Print printStmt() {
        int line = previous().line;
        Expr expr = expression();
        require(TokenType.SEMICOLON, "expected ';' after print statement");
        return new Stmt.Print(line, expr);
    }

    private Stmt.Expression exprStmt() {
        int line = peek().line; // TODO: Like this?
        Expr expr = expression();
        require(TokenType.SEMICOLON, "expected ';' after expression statement");
        return new Stmt.Expression(line, expr);
    }

    private Expr expression() {
        if (match(TokenType.INT_NUMBER)) return intNumber();
        else if (match(TokenType.IDENTIFIER)) return var();
        else if (match(TokenType.NIL)) return nil();
        else if (match(TokenType.TRUE, TokenType.FALSE)) return bool();
        else if (match(TokenType.CHARACTER)) return character();
        else {
            // TODO: Test this
            errorAtPeek("expected expression"); return null;
        }
    }

    private Expr var() {
        return new Expr.Var(previous().line, previous().text);
    }

    private Expr intNumber() {
        return new Expr.IntLiteral(previous().line, Integer.parseInt(previous().text));
    }

    private Expr nil() {
        return new Expr.NilLiteral(previous().line);
    }

    private Expr bool() {
        return new Expr.BoolLiteral(previous().line, previous().type == TokenType.TRUE);
    }

    private Expr character() {
        return new Expr.CharLiteral(previous().line, previous().text.charAt(0));
    }

    private void skipSemicolons() {
        while (!isAtEnd() && peek().type == TokenType.SEMICOLON) {
            advance();
        }
    }

    private void error(int line, String msg) {
        errorListener.reportError(line, msg);
        throw new ParserError();
    }

    //private void errorAtPrevious(String msg) {
    //  error(previous().line, msg);
    //}

    private void errorAtPeek(String msg) {
        error(peek().line, msg);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (peek().type == type) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token require(TokenType type, String errorMsg) {
        if (match(type)) {
            return previous();
        } else {
            errorAtPeek(errorMsg); return null;
        }
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token previous() {
        return tokens.get(pos - 1);
    }

    private void advance() {
        pos++;
    }

    private boolean isAtEnd() {
        return tokens.get(pos).type == TokenType.EOF;
    }
}
