package com.inanyan.sl.lang;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.util.ErrorListener;

import java.io.PrintStream;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final PrintStream out;
    private final ErrorListener errorListener;

    public Interpreter(ErrorListener errorListener, PrintStream out) {
        this.errorListener = errorListener;
        this.out = out;
    }

    public void run(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            execute(stmt);
        }
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }

    public Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitIntLiteral(Expr.IntLiteral expr) {
        return expr.value;
    }

    @Override
    public Object visitVar(Expr.Var expr) {
        // TODO: To be implemented
        return null;
    }

    @Override
    public Void visitExpr(Stmt.Expression stmt) {
        evaluate(stmt.expr);
        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print stmt) {
        Object result = evaluate(stmt.expr);
        String str = Rules.stringify(result);
        this.out.println(str);
        return null;
    }
}
