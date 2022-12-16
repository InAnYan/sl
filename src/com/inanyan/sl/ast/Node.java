package com.inanyan.sl.ast;

public abstract class Node {
    public final int line;

    public Node(int line) {
        this.line = line;
    }

    public abstract boolean fullyCompareTo(Object stmt);
}
