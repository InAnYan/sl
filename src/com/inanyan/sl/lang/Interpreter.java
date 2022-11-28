package com.inanyan.sl.lang;

import com.inanyan.sl.ast.Stmt;

import java.util.List;

public class Interpreter {
    public void run(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            execute(stmt);
        }
    }

    private void execute(Stmt stmt) {

    }
}
