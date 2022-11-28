package com.inanyan.jsl;

import com.inanyan.sl.lang.Interpreter;
import com.inanyan.sl.parsing.Lexer;
import com.inanyan.sl.parsing.Parser;
import com.inanyan.sl.util.ErrorListener;

public class Main {
    private final static ErrorListener errorListener = new ErrorListener() {

        @Override
        public void reportError(int line, String msg) {

        }

        @Override
        public void reportWarning(int line, String msg) {

        }
    };

    public static void main(String[] args) {
        System.out.println("Hello!");

        Lexer lexer = new Lexer(errorListener, ";");
        Parser parser = new Parser(errorListener, lexer.scanTokens());
        Interpreter interpreter = new Interpreter();
        interpreter.run(parser.parse());


        System.out.println("Bye!");
    }
}