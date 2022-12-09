package com.inanyan.sl.test;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Stmt;

public class TestVisitor implements Expr.Visitor<Integer>, Stmt.Visitor<Integer> {

    public int visit(Expr expr) {
        return expr.accept(this);
    }

    public int visit(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public Integer visitIntLiteral(Expr.IntLiteral expr) {
        return 1;
    }

    @Override
    public Integer visitVar(Expr.Var expr) {
        return 1;
    }

    @Override
    public Integer visitExpr(Stmt.Expression stmt) {
        return 1 + visit(stmt.expr);
    }

    @Override
    public Integer visitPrint(Stmt.Print stmt) {
        return 1 + visit(stmt.expr);
    }
}
