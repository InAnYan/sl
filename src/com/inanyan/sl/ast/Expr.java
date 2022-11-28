package com.inanyan.sl.ast;

public abstract class Expr extends Node {
    public abstract <R> R accept(Visitor<R> visitor);

    public Expr(int line) {
        super(line);
    }

    public static class IntLiteral extends Expr {
        public IntLiteral(int line, int value) {
            super(line);
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIntLiteral(this);
        }

        public int value;
    }

    public static class Var extends Expr {
        public Var(int line, String text) {
            super(line);
            this.text = text;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVar(this);
        }

        public String text;
    }

    public interface Visitor<R> {
        R visitIntLiteral(IntLiteral expr);
        R visitVar(Var expr);
    }
}
