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

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof IntLiteral comp)) {
                return false;
            }

            return comp.line == this.line && comp.value == this.value;
        }
    }

    public static class FloatLiteral extends Expr {
        public FloatLiteral(int line, double value) {
            super(line);
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFloatLiteral(this);
        }

        public double value;

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof FloatLiteral comp)) {
                return false;
            }

            return comp.line == this.line && comp.value == this.value;
        }
    }

    public static class BoolLiteral extends Expr {
        public BoolLiteral(int line, boolean value) {
            super(line);
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBoolLiteral(this);
        }

        public boolean value;

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof BoolLiteral comp)) {
                return false;
            }

            return comp.line == this.line && comp.value == this.value;
        }
    }

    public static class StringLiteral extends Expr {
        public StringLiteral(int line, String value) {
            super(line);
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitStringLiteral(this);
        }

        public String value;

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof StringLiteral comp)) {
                return false;
            }

            return comp.line == this.line && comp.value.equals(this.value);
        }
    }

    public static class CharLiteral extends Expr {
        public CharLiteral(int line, char value) {
            super(line);
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCharLiteral(this);
        }

        public char value;

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof CharLiteral comp)) {
                return false;
            }

            return comp.line == this.line && comp.value == this.value;
        }
    }

    public static class NilLiteral extends Expr {
        public NilLiteral(int line) {
            super(line);
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitNilLiteral(this);
        }

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof NilLiteral comp)) {
                return false;
            }

            return comp.line == this.line;
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
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof Var comp)) {
                return false;
            }

            return comp.line == this.line && comp.text.equals(this.text);
        }

        public String text;
    }

    public static class Unary extends Expr {
        public Unary(int line, Op op, Expr expr) {
            super(line);
            this.op = op;
            this.expr = expr;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }

        @Override
        public boolean fullyCompareTo(Object stmt) {
            if (!(stmt instanceof Unary comp)) {
                return false;
            }

            return comp.line == this.line && comp.op == this.op && comp.expr.fullyCompareTo(this.expr);
        }

        public static enum Op {
            NOT, NEGATE, PLUS,

            BITWISE_NOT
        }

        public Op op;
        public Expr expr;
    }

    public interface Visitor<R> {
        R visitIntLiteral(IntLiteral expr);
        R visitFloatLiteral(FloatLiteral expr);
        R visitBoolLiteral(BoolLiteral expr);
        R visitStringLiteral(StringLiteral expr);
        R visitCharLiteral(CharLiteral expr);
        R visitNilLiteral(NilLiteral expr);
        R visitVar(Var expr);
        R visitUnary(Unary expr);
    }
}
