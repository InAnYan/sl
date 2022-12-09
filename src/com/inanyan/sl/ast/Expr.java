package com.inanyan.sl.ast;

import org.junit.experimental.categories.Categories;

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

        @Override
        public boolean compareTo(Object stmt) {
            if (!(stmt instanceof IntLiteral)) {
                return false;
            }

            IntLiteral comp = (IntLiteral) stmt;

            return comp.line == this.line && comp.value == this.value;
        }
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

        @Override
        public boolean compareTo(Object stmt) {
            if (!(stmt instanceof Var)) {
                return false;
            }

            Var comp = (Var) stmt;

            return comp.line == this.line && comp.text.equals(this.text);
        }

        public String text;
    }

    public interface Visitor<R> {
        R visitIntLiteral(IntLiteral expr);
        R visitVar(Var expr);
    }
}
