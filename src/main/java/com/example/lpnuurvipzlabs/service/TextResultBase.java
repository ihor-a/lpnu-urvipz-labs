package com.example.lpnuurvipzlabs.service;

import java.text.DecimalFormat;
import java.util.Arrays;

abstract class TextResultBase {
    protected String result = "";

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

    protected void appendResultValue(String description, Double[] value) {
        var displayArr = Arrays.stream(value).map(val -> new DecimalFormat("#.###").format(val)).toArray();
        appendResultText(String.format("%s:", description));
        appendResultText(Arrays.toString(displayArr));
    }

    protected void appendResultValue(String description, double value, int indentLen) {
        String indent = indentLen > 0 ? " ".repeat(indentLen - description.length()) : " ";
        appendResultText(String.format("%s:%s%f", description, indent, value));
    }

    protected void appendResultNewline() {
        appendResultText("");
    }

    protected void resetResult() {
        result = "";
    }
}
