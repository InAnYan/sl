package com.inanyan.sl.lang;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Environment enclosing;
    private final Map<String, Object> map = new HashMap<>();

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object lookup(String key) {
        Object res = map.get(key);

        if (res == null && enclosing != null) {
            return enclosing.lookup(key);
        }

        return res;
    }

    boolean define(String key, Object obj) {
        return map.put(key, obj) == null;
    }
}
