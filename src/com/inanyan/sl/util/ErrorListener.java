package com.inanyan.sl.util;

public interface ErrorListener {
    void reportError(int line, String msg);
    void reportWarning(int line, String msg);
}
