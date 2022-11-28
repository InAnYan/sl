package com.inanyan.sl.test;

import com.inanyan.sl.util.ErrorListener;

public class TestsErrorListener implements ErrorListener {
    private int errorsCount = 0;
    private int warningsCount = 0;
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
    }

    @Override
    public void reportWarning(int line, String msg) {
        warningsCount++;
    }
}
