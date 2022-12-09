package com.inanyan.sl.test;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Node;
import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.parsing.Lexer;
import com.inanyan.sl.parsing.Parser;
import com.inanyan.sl.parsing.Rules;
import com.inanyan.sl.parsing.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private final TestsErrorListener errorListener = new TestsErrorListener();
    private List<Stmt> stmts;

    private void generate(String src) {
        errorListener.resetCounters();
        Lexer lexer = new Lexer(errorListener, src);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(errorListener, tokens);
        stmts = parser.parse();
    }

    private void generateAndCheck(String src, int expectedCount) {
        generate(src);
        assertNotNull(stmts);
        assertEquals(expectedCount, stmts.size());
    }

    private void noErrors() {
        assertEquals(0, errorListener.getErrorsCount());
    }

    private void noWarnings() {
        assertEquals(0, errorListener.getWarningsCount());
    }

    private void noErrorsAndWarnings() {
        noErrors();
        noWarnings();
    }

    private void shouldBeErrors(int count) {
        assertEquals(count, errorListener.getErrorsCount());
    }

    private void shouldBeWarnings(int count) {
        assertEquals(count, errorListener.getErrorsCount());
    }

    private void assertNode(int index, Node node) {
        assertTrue(stmts.get(index).compareTo(node));
    }

    @Test
    void empty() {
        generateAndCheck("", 0);
        noErrorsAndWarnings();
    }

    @Test
    void semicolonsWithSpaces() {
        generateAndCheck("  \t;; ;\r\n ;\r ; \t;;;; ;;;\t \t;\n", 0);
        noErrorsAndWarnings();
    }

    @Test
    void basicExprStmt() {
        generateAndCheck("123;", 1);
        assertNode(0, new Stmt.Expression(0, new Expr.IntLiteral(0, 123)));
    }

    @Test
    void exprStmtWithoutSemicolon() {
        generate("123");
        shouldBeErrors(1);
        noWarnings();
    }

    @Test
    void twoIntegersInExpr() {
        generate("123 123 ;");
        noWarnings();
        shouldBeErrors(1);
    }

    @Test
    void syncTwoExprStmts() {
        generate("123 2 ; 123");
        noWarnings();
        shouldBeErrors(2);
    }

    @Test
    void varExprStmt() {
        generateAndCheck("abc;", 1);
        noErrorsAndWarnings();
        assertNode(0, new Stmt.Expression(0, new Expr.Var(0, "abc")));
    }

    @Test
    void varAndNumExpr() {
        generateAndCheck("abc;\n123;", 2);
        noErrorsAndWarnings();
        assertNode(0, new Stmt.Expression(0, new Expr.Var(0, "abc")));
        assertNode(1, new Stmt.Expression(1, new Expr.IntLiteral(1, 123)));
    }

    @Test
    void printStmt() {
        generateAndCheck("print abc;", 1);
        noErrorsAndWarnings();
        assertNode(0, new Stmt.Print(0, new Expr.Var(0, "abc")));
    }

    @Test
    void printWithoutExpr() {
        generate("print ;");
        noWarnings();
        shouldBeErrors(1);
    }

    @Test
    void exprAndPrintStmts() {
        generateAndCheck("print a;\n \t_b; \t\n\t123;\n\r", 3);
        noErrorsAndWarnings();
        assertNode(0, new Stmt.Print(0, new Expr.Var(0, "a")));
        assertNode(1, new Stmt.Expression(1, new Expr.Var(1, "_b")));
        assertNode(2, new Stmt.Expression(2, new Expr.IntLiteral(2, 123)));
    }

    @Test
    void excludeAndCheckSomeMethods() {
        // Accept, compare in AST
        // Rules
        // Tests compareTo, but poorly

        Expr.Var var = new Expr.Var(0, "abc");
        assertFalse(var.compareTo(9));
        Stmt.Print print = new Stmt.Print(0, var);
        assertTrue(print.compareTo(new Stmt.Print(0, new Expr.Var(0, "abc"))));
        assertFalse(print.compareTo(10));

        Expr.IntLiteral intLiteral = new Expr.IntLiteral(0, 10);
        assertFalse(intLiteral.compareTo(9));
        Stmt.Expression exprS = new Stmt.Expression(1, intLiteral);
        assertTrue(exprS.compareTo(new Stmt.Expression(1, new Expr.IntLiteral(0, 10))));
        assertFalse(exprS.compareTo(10));

        Rules.isAlphabetic('a');
        Rules.isDigit('a');
        Rules.isAlphaDigit('a');

        TestVisitor visitor = new TestVisitor();
        assertEquals(1, visitor.visit(var));
        assertEquals(2, visitor.visit(print));
        assertEquals(1, visitor.visit(intLiteral));
        assertEquals(2, visitor.visit(exprS));
    }
}