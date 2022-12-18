package com.inanyan.sl.lang;

import com.inanyan.sl.ast.Expr;
import com.inanyan.sl.ast.Stmt;
import com.inanyan.sl.util.ErrorListener;

import java.io.PrintStream;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private final PrintStream out;
    private Environment currentEnvironment;

    public Interpreter(PrintStream out, Environment environment) {
        this.currentEnvironment = environment;
        this.out = out;
    }
    public Interpreter(PrintStream out) {
        this.currentEnvironment = new Environment(Builtins.createGlobalEnvironment());
        this.out = out;
    }

    public static class Error extends RuntimeException {
        public final int line;
        public final String msg;
        public Error(int line, String msg) {
            super(msg);
            this.line = line;
            this.msg = msg;
        }
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
    public Object visitFloatLiteral(Expr.FloatLiteral expr) {
        return expr.value;
    }

    @Override
    public Object visitBoolLiteral(Expr.BoolLiteral expr) {
        return expr.value;
    }

    @Override
    public Object visitStringLiteral(Expr.StringLiteral expr) {
        return expr.value;
    }

    @Override
    public Object visitCharLiteral(Expr.CharLiteral expr) {
        return expr.value;
    }

    @Override
    public Object visitNilLiteral(Expr.NilLiteral expr) {
        return null;
    }

    @Override
    public Object visitVar(Expr.Var expr) {
        Object obj = currentEnvironment.lookup(expr.text);
        if (obj == null) {
            throw new Error(expr.line, "undefined reference to '" + expr.text + "'");
        }
        return obj;
    }

    @Override
    public Object visitUnary(Expr.Unary expr) {
        Object evaluated = evaluate(expr.expr);

        return switch (expr.op) {
            case NOT -> {
                if (evaluated instanceof Boolean bool) {
                    yield !bool;
                } else {
                    throw new Error(expr.line, "can't negate '" + evaluated.getClass().getName() + "'");
                }
            }
            case NEGATE -> {
                if (evaluated instanceof Integer num) {
                    yield -num;
                } else if (evaluated instanceof Double num) {
                    yield -num;
                } else {
                    throw new Error(expr.line, "can't negate '" + evaluated.getClass().getName() + "'");
                }
            }
            case PLUS -> {
                if (evaluated instanceof Integer num) {
                    yield +num;
                } else if (evaluated instanceof Double num) {
                    yield +num;
                } else {
                    throw new Error(expr.line, "can't do plus for '" + evaluated.getClass().getName() + "'");
                }
            }
            case BITWISE_NOT -> {
                if (evaluated instanceof Integer num) {
                    yield ~num;
                } else {
                    throw new Error(expr.line, "can't perform bitwise not for '"
                            + evaluated.getClass().getName() + "'");
                }
            }
        };
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
