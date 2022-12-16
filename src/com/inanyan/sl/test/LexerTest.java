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

    private void generateTokensAndCheck(String text, int expectedLength) {
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
        assertEquals(count, errorListener.getWarningsCount());
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
        generateTokensAndCheck("", 1);
        noErrorsAndWarnings();
    }

    @Test
    void someSpaces() {
        generateTokensAndCheck("   \n \n  \n \t   \n \t \r \r \t \n ", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lotsOfSemicolons() {
        generateTokensAndCheck(";;;;;;;;;;;;;;;;;;;;;;;;;;;", 28);
        noErrorsAndWarnings();
        inRange(0, 26, TokenType.SEMICOLON);
        inRange(0, 26, 0);
    }

    @Test
    void spacesAndSemicolons() {
        generateTokensAndCheck("; ;  \n ; ;     ;\n ;\r  \r  \t  ;;\t;;;;\t\n;;\r;", 16);
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
        generateTokensAndCheck("# HI!", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lineCommentAndNewLine() {
        generateTokensAndCheck("# HI!\n", 1);
        noErrorsAndWarnings();
    }

    @Test
    void lineCommentAndSemicolons() {
        generateTokensAndCheck("; ; ; ; ;# HI!\n;\n ;  ; ;", 10);
        noErrorsAndWarnings();
        inRange(0, 8, TokenType.SEMICOLON);

        inRange(0, 4, 0);
        assertLine(5,     1);
        inRange(6, 8, 2);
    }

    @Test
    void someLineCommentsInCode() {
        generateTokensAndCheck("; # hhh\n ; #;\n; ; ;# HI!\n;\n   ; ;#BYE", 9);
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
        generateTokensAndCheck("123", 2);
        noErrorsAndWarnings();
        checkIntNumber(0, 123);
        assertLine(0, 0);
    }

    @Test
    void threeNumbersAndSpaces() {
        generateTokensAndCheck("    0 \r 7\t \n  28 ", 4);
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
        generateTokensAndCheck("bla_Bla123", 2);
        noErrorsAndWarnings();
        checkIdentifier(0, "bla_Bla123");
    }

    @Test
    void threeIdentifiersAndSapces() {
        generateTokensAndCheck("a2sb \t\n__asd\n\r  \t\r \t \tppek__\n", 4);
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
        generateTokensAndCheck("\tprint\n\r", 2);
        assertType(0, TokenType.PRINT);
        assertLine(0, 0);
    }

    @Test
    void printStatement() {
        generateTokensAndCheck("print abc;", 4);
        noErrorsAndWarnings();
        assertLine(0, 0);
        assertLine(1, 0);
        assertLine(2, 0);
        assertType(0, TokenType.PRINT);
        assertType(1, TokenType.IDENTIFIER);
        assertText(1, "abc");
        assertType(2, TokenType.SEMICOLON);
    }

    @Test
    void nilKeyword() {
        generateTokensAndCheck(" nil\n", 2);
        noErrorsAndWarnings();
        assertType(0, TokenType.NIL);
        assertLine(0, 0);
    }

    @Test
    void printNil() {
        generateTokensAndCheck("\t \rprint \n  nil;\n", 4);
        noErrorsAndWarnings();
        assertType(0, TokenType.PRINT);
        assertLine(0, 0);
        assertType(1, TokenType.NIL);
        assertLine(1, 1);
        assertType(2, TokenType.SEMICOLON);
        assertLine(2, 1);
    }

    @Test
    void boolKeyword() {
        generateTokensAndCheck(" \t true  \n\r\tfalse \n", 3);
        noErrorsAndWarnings();
        assertType(0, TokenType.TRUE);
        assertLine(0, 0);
        assertType(1, TokenType.FALSE);
        assertLine(1, 1);
    }

    @Test
    void boolButIdentifier() {
        generateTokensAndCheck(" \t True  \n\r\tFalse \n", 3);
        noErrorsAndWarnings();
        assertType(0, TokenType.IDENTIFIER);
        assertLine(0, 0);
        assertType(1, TokenType.IDENTIFIER);
        assertLine(1, 1);
    }

    @Test
    void simpleChar() {
        generateTokensAndCheck("'c'", 2);
        noErrorsAndWarnings();
        assertType(0, TokenType.CHARACTER);
        assertText(0, "c");
        assertLine(0, 0);
    }

    @Test
    void escapedChars() {
        generateTokensAndCheck("'\\t' '\\r' '\\n'", 4);
        noErrorsAndWarnings();

        assertType(0, TokenType.CHARACTER);
        assertText(0, "\t");
        assertLine(0, 0);

        assertType(1, TokenType.CHARACTER);
        assertText(1, "\r");
        assertLine(1, 0);

        assertType(2, TokenType.CHARACTER);
        assertText(2, "\n");
        assertLine(2, 0);
    }

    @Test
    void unknownEscaped() {
        generateTokensAndCheck("'\\p'", 2);
        noErrors();
        shouldBeWarnings(1);
        assertType(0, TokenType.CHARACTER);
        assertText(0, "p");
        assertLine(0, 0);
    }

    @Test
    void unterminatedChar() {
        generateTokens("print 'c\n\r 't'");
        shouldBeErrors(1);
    }

    @Test
    void characterBigTest() {
        generateTokensAndCheck("print 'c'; \n\t'\t'; \n'\\\\';", 8);
        noErrorsAndWarnings();

        assertType(0, TokenType.PRINT);
        assertType(1, TokenType.CHARACTER);
        assertType(2, TokenType.SEMICOLON);
        assertType(3, TokenType.CHARACTER);
        assertType(4, TokenType.SEMICOLON);
        assertType(5, TokenType.CHARACTER);
        assertType(6, TokenType.SEMICOLON);

        assertLine(0, 0);
        assertLine(1, 0);
        assertLine(2, 0);
        assertLine(3, 1);
        assertLine(4, 1);
        assertLine(5, 2);
        assertLine(6, 2);

        assertText(1, "c");
        assertText(3, "\t");
        assertText(5, "\\");
    }

    @Test
    void string() {
        generateTokensAndCheck("\"abc\"", 2);
        noErrorsAndWarnings();

        assertType(0, TokenType.STRING);
        assertText(0, "abc");
        assertLine(0, 0);
    }

    @Test
    void printString() {
        generateTokensAndCheck("\tprint \"lox\";\n", 4);
        noErrorsAndWarnings();

        assertType(0, TokenType.PRINT);
        assertLine(0, 0);

        assertType(1, TokenType.STRING);
        assertText(1, "lox");
        assertLine(1, 0);

        assertType(2, TokenType.SEMICOLON);
        assertLine(2, 0);
    }

    @Test
    void unterminatedString() {
        generateTokens("print \"ada fas das das sa das ;");
        noWarnings();
        shouldBeErrors(1);
    }

    @Test
    void floatNumber() {
        generateTokensAndCheck("3.14", 2);
        noErrorsAndWarnings();

        assertLine(0, 0);
        assertText(0, "3.14");
        assertType(0, TokenType.FLOAT_NUMBER);
    }

    @Test
    void floatAndSemicolon() {
        generateTokensAndCheck("\n 123.09   \t;", 3);
        noErrorsAndWarnings();

        assertLine(0, 1);
        assertText(0, "123.09");
        assertType(0, TokenType.FLOAT_NUMBER);

        assertLine(1, 1);
        assertType(1, TokenType.SEMICOLON);
    }
}