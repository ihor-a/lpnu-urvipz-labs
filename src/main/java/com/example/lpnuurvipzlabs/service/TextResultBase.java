package com.example.lpnuurvipzlabs.service;

import java.util.Arrays;

abstract class TextResultBase {
    protected String result;

    public String getResult() {
        return result;
    }

    protected void appendResultText(String message) {
        if (result.length() == 0) {
            result = String.format("%s", message);
        } else {
            result = String.format("%s%n%s", result, message);
        }
    }

    protected void appendResultValue(String description, double[] value) {
        appendResultText(description);
        appendResultText(Arrays.toString(value));
    }

    protected void appendResultNewline() {
        appendResultText("");
    }

    protected void resetResult() {
        result = "";
    }
}
