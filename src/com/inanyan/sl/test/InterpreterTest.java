package com.inanyan.sl.test;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.lang.Interpreter;
import com.inanyan.sl.parsing.Lexer;
import com.inanyan.sl.parsing.Parser;
import com.inanyan.sl.parsing.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private final TestsErrorListener errorListener = new TestsErrorListener();
    private List<Stmt> parse(String str) {
        Lexer lexer = new Lexer(errorListener, str);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(errorListener, tokens);
        return parser.parse();
    }
    private Object evaluate(Expr expr) {
        Interpreter interpreter = new Interpreter(null);
        return interpreter.evaluate(expr);
    }
    @Test
    void intExpr() {
        Object res = evaluate(parse("123").get(0));
    }
}