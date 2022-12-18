package com.inanyan.sl.lang;

public class Builtins {
    public static Environment createGlobalEnvironment() {
        Environment global = new Environment();

        global.define("SL_VER_MAJOR", 0);
        global.define("SL_VER_MINOR", 1);
        global.define("SL_VER_PATCH", 1);
        global.define("SL_VER_STR", "0.1.0");

        return global;
    }
}
