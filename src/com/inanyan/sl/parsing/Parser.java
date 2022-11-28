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
        advance();
        if (previous().type == TokenType.SEMICOLON) return;
        switch (peek().type) {
            case PRINT:
            case EOF:
                break;
            default:
                advance();
                break;
        }
    }

    private Stmt statement() {
        skipSemicolons();

        if (match(TokenType.PRINT)) return printStmt();
        else return exprStmt();
    }

    private Stmt.Print printStmt() {
        int line = previous().line;
        Expr expr = expression();
        return new Stmt.Print(line, expr);
    }

    private Stmt.Expression exprStmt() {
        int line = peek().line; // TODO: Like this?
        Expr expr = expression();
        return new Stmt.Expression(line, expr);
    }

    private Expr expression() {
        if (match(TokenType.INT_NUMBER)) return intNumber();
        else if (match(TokenType.IDENTIFIER)) return var();
        else {
            errorAtPeek("expected expression");
            return null;
        }
    }

    private Expr var() {
        return new Expr.Var(previous().line, previous().text);
    }

    private Expr intNumber() {
        try {
            return new Expr.IntLiteral(previous().line, Integer.parseInt(previous().text));
        } catch (NumberFormatException e) {
            errorAtPrevious("can not parse integer literal");
            return null;
        }
    }

    private void skipSemicolons() {
        while (match(TokenType.SEMICOLON));
    }

    private void error(int line, String msg) {
        errorListener.reportError(line, msg);
    }

    private void errorAtPrevious(String msg) {
        error(previous().line, msg);
    }

    private void errorAtPeek(String msg) {
        error(peek().line, msg);
    }

    private boolean match(TokenType type) {
        if (peek().type == type) {
            advance();
            return true;
        }
        return false;
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
