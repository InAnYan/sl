package com.inanyan.sl.test;

import com.inanyan.sl.parsing.Lexer;
import com.inanyan.sl.parsing.Token;
import com.inanyan.sl.parsing.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    private final TestsErrorListener errorListener = new TestsErrorListener();
    private List<Token> result;

    private void assertType(int index, TokenType type) {
        assertEquals(type, result.get(index).type);
    }

    private void assertLine(int index, int expectedLine) {
        assertEquals(expectedLine, result.get(index).line);
    }

    private void assertText(int index, String text) {
        assertEquals(text, result.get(index).text);
    }

    private void assertTextEmpty(int index) {
        assertText(index, "");
    }

    private void checkLength(List<Token> result, int expected) {
        assertEquals(expected, result.size());
    }

    private void checkEOF(List<Token> result) {
        assertType(result.size() - 1, TokenType.EOF);
    }

    private void checkTokens(List<Token> result, int expectedLength) {
        assertNotNull(result);
        checkLength(result, expectedLength);
        checkEOF(result);
    }

    private void generateTokens(String text) {
        errorListener.resetCounters();
        Lexer lexer = new Lexer(errorListener, text);
        result = lexer.scanTokens();
    }

    private void generateAndCheckTokens(String text, int expectedLength) {
        generateTokens(text);
        checkTokens(result, expectedLength);
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

    private void checkIntNumber(int index, int number) {
        assertType(index, TokenType.INT_NUMBER);
        assertText(index, String.valueOf(number));
    }

    private void checkIdentifier(int index, String text) {
        assertType(index, TokenType.IDENTIFIER);
        assertText(index, text);
    }

    private void inRange(int start, int end, TokenType type) {
        for (int i = start; i <= end; i++) {
            assertType(i, type);
        }
    }

    private void inRange(int start, int end, int expectedLine) {
        for (int i = start; i <= end; i++) {
            assertLine(i, expectedLine);
        }
    }

    @Test
    void empty() {
        generateAndCheckTokens("", 1);
        noErrorsAndWarnings();
    }

    @Test
    void someSpaces() {
        generateAndCheckTokens("   \n \n  \n \t   \n \t \r \r \t \n ", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lotsOfSemicolons() {
        generateAndCheckTokens(";;;;;;;;;;;;;;;;;;;;;;;;;;;", 28);
        noErrorsAndWarnings();
        inRange(0, 26, TokenType.SEMICOLON);
        inRange(0, 26, 0);
    }

    @Test
    void spacesAndSemicolons() {
        generateAndCheckTokens("; ;  \n ; ;     ;\n ;\r  \r  \t  ;;\t;;;;\t\n;;\r;", 16);
        noErrorsAndWarnings();
        inRange(0, 14, TokenType.SEMICOLON);

        inRange(0,  1,  0);
        inRange(2,  4,  1);
        inRange(5,  11, 2);
        inRange(12, 14, 3);
    }

    @Test
    void illegalChars() {
        generateTokens("^$");
        noWarnings();
        shouldBeErrors(2);
    }

    @Test
    void illegalCharsWithSpaces() {
        generateTokens("  ^ ^\n\r   \n  \t$ \t  ");
        noWarnings();
        shouldBeErrors(3);
    }

    @Test
    void illegalCharsAndSemicolons() {
        generateTokens("  ^\n;;;;;;;;;;\r ;;;;;;;;;;  \n  \t$;;;;;; \t  ");
        noWarnings();
        shouldBeErrors(2);
    }

    @Test
    void lineCommentIsWhole() {
        generateAndCheckTokens("# HI!", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lineCommentAndNewLine() {
        generateAndCheckTokens("# HI!\n", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lineCommentAndSemicolons() {
        generateAndCheckTokens("; ; ; ; ;# HI!\n;\n ;  ; ;", 10);
        noErrorsAndWarnings();
        inRange(0, 8, TokenType.SEMICOLON);

        inRange(0, 4, 0);
        assertLine(5,     1);
        inRange(6, 8, 2);
    }

    @Test
    void someLineCommentsInCode() {
        generateAndCheckTokens("; # hhh\n ; #;\n; ; ;# HI!\n;\n   ; ;#BYE", 9);
        noErrorsAndWarnings();
        inRange(0, 7, TokenType.SEMICOLON);

        assertLine(0,     0);
        assertLine(1,     1);
        inRange(2, 4, 2);
        assertLine(5,     3);
        inRange(6, 7, 4);
    }

    @Test
    void basicNumber() {
        generateAndCheckTokens("123", 2);
        noErrorsAndWarnings();
        checkIntNumber(0, 123);
        assertLine(0, 0);
    }

    @Test
    void threeNumbersAndSpaces() {
        generateAndCheckTokens("    0 \r 7\t \n  28 ", 4);
        noErrorsAndWarnings();
        checkIntNumber(0, 0);
        assertLine(0, 0);
        checkIntNumber(1, 7);
        assertLine(1, 0);
        checkIntNumber(2, 28);
        assertLine(2, 1);
    }

    @Test
    void basicIdentifier() {
        generateAndCheckTokens("bla_Bla123", 2);
        noErrorsAndWarnings();
        checkIdentifier(0, "bla_Bla123");
    }

    @Test
    void threeIdentifiersAndSapces() {
        generateAndCheckTokens("a2sb \t\n__asd\n\r  \t\r \t \tppek__\n", 4);
        noErrorsAndWarnings();
        checkIdentifier(0, "a2sb");
        assertLine(0, 0);
        checkIdentifier(1, "__asd");
        assertLine(1, 1);
        checkIdentifier(2, "ppek__");
        assertLine(2, 2);
    }

    @Test
    void printKeyword() {
        generateAndCheckTokens("print", 2);
        assertType(0, TokenType.PRINT);
        assertLine(0, 0);
    }

    @Test
    void printStatement() {
        generateAndCheckTokens("print abc;", 4);
        noErrorsAndWarnings();
        assertLine(0, 0);
        assertLine(1, 0);
        assertLine(2, 0);
        assertType(0, TokenType.PRINT);
        assertType(1, TokenType.IDENTIFIER);
        assertText(1, "abc");
        assertType(2, TokenType.SEMICOLON);
    }
}