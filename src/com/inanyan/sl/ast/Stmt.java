package com.inanyan.sl.ast;

public abstract class Stmt extends Node {
    public abstract <R> R accept(Visitor<R> visitor);

    public Stmt(int line) {
        super(line);
    }

    public static class Expression extends Stmt {
        public Expression(int line, Expr expr) {
            super(line);
            this.expr = expr;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpr(this);
        }

        public final Expr expr;
    }

    public static class Print extends Stmt {
        public Print(int line, Expr expr) {
            super(line);
            this.expr = expr;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrint(this);
        }

        public final Expr expr;
    }

    public interface Visitor<R> {
        R visitExpr(Expression stmt);
        R visitPrint(Print stmt);
    }
}
