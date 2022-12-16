package com.inanyan.sl.test;

import com.inanyan.sl.util.ErrorListener;

import java.util.ArrayList;
import java.util.List;

public class TestsErrorListener implements ErrorListener {
    private int errorsCount = 0;
    private int warningsCount = 0;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    public void resetCounters() {
        warningsCount = 0;
        errorsCount = 0;
    }

    public int getErrorsCount() {
        return errorsCount;
    }

    public int getWarningsCount() {
        return warningsCount;
    }

    @Override
    public void reportError(int line, String msg) {
        errorsCount++;
        errors.add(line + " : " + msg);
    }

    @Override
    public void reportWarning(int line, String msg) {
        warningsCount++;
        warnings.add(line + " : " + msg);
    }
}
