package com.inanyan.jsl;

import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.lang.Interpreter;
import com.inanyan.sl.parsing.Lexer;
import com.inanyan.sl.parsing.Parser;
import com.inanyan.sl.parsing.Token;
import com.inanyan.sl.util.ErrorListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    private static boolean hadError = false;
    private static String currentSource;
    private final static ErrorListener errorListener = new ErrorListener() {

        @Override
        public void reportError(int line, String msg) {
            report(line, "error", msg);
            hadError = true;
        }

        @Override
        public void reportWarning(int line, String msg) {
            report(line, "warning", msg);
        }

        private void report(int line, String what, String msg) {
            System.out.println(currentSource + ":" + String.valueOf(line) + ": " + what + ": " + msg + ".");
        }
    };

    private final static Interpreter interpreter = new Interpreter(errorListener, System.out);

    public static void main(String[] args) {
        if (args.length > 1) {
            // TODO: '<main>' name
            System.out.println("Usage: <main> filename - runs the file");
            System.out.println("   or  <main>          - runs a REPL");
            System.exit(1);
        } else if (args.length == 1){
            if (!loadFile(args[0])) {
                System.out.println("Errors occurred while file was loading. Exiting...");
                System.exit(2);
            }
            // TODO: Load file
        } else {
            printWelcomeMsg();

            if (!startRepl()) {
                System.out.println("Errors occurred while file consulting. Exiting...");
                System.exit(3);
            }
        }
    }

    private static boolean loadFile(String path) {
        currentSource = path;
        throw new RuntimeException("Unimplemented loadFile");
    }

    private static void printWelcomeMsg() {
        System.out.println("jsl - SL interpreter in Java.");
        System.out.println("Copyright (c) 2022 InAnYan.");
        System.out.println("To exit press Ctrl-C or type ':quit' or ':exit' (without quotes).");
        System.out.print('\n');
    }

    private static boolean startRepl() {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        currentSource = "<repl>";
        hadError = false;

        while (true) {
            System.out.print("> ");
            try {
                String line = reader.readLine();

                if (line == null) return true;
                else if (line.isEmpty()) continue;
                else if (line.charAt(0) == ':') {
                    if (line.equals(":quit") || line.equals(":exit"))
                        return true;
                }

                runStr(line);
                hadError = false;
            } catch (IOException e) {
                // TODO: Print e?
                return false;
            }
        }
    }

    private static void runStr(String str) {
        Lexer lexer = new Lexer(errorListener, str);
        List<Token> tokens = lexer.scanTokens();
        if (hadError) return;

        Parser parser = new Parser(errorListener, tokens);
        List<Stmt> stmts = parser.parse();
        if (hadError) return;

        interpreter.run(stmts);
    }
}