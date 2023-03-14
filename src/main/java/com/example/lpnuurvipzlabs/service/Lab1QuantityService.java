package com.example.lpnuurvipzlabs.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Lab1QuantityService extends Lab1Base {

    protected QuantityValues mP1Quantity, mP2Quantity, mP3Quantity;

    public String calculate() {
        resetResult();

        mP1Quantity = makeQuantityOfVariants(mzkK3K4ToP1TenseStates, mQuantity[2], mQuantity[3]);
        appendResultQuantity("Quantity of tense states for P1 indicator", mP1Quantity);
        appendResultParamValue("SUM", mP1Quantity.getSum());
        appendResultNewline();

        mP2Quantity = makeQuantityOfVariants(mzkK2P1ToP2TenseStates, mQuantity[1], mP1Quantity.getValues());
        appendResultQuantity("Quantity of tense states for P2 indicator", mP2Quantity);
        appendResultParamValue("SUM", mP2Quantity.getSum());
        appendResultNewline();

        mP3Quantity = makeQuantityOfVariants(mzkK1P2ToP3TenseStates, mQuantity[0], mP2Quantity.getValues());
        appendResultQuantity("Quantity of tense states for P3 indicator", mP3Quantity);
        appendResultParamValue("SUM", mP3Quantity.getSum());
        appendResultNewline();

        appendResultParamValue("Total quantity of tense states",
                mP1Quantity.getSum() * mP2Quantity.getSum() * mP3Quantity.getSum()
        );

        return getResult();
    }

    private QuantityValues makeQuantityOfVariants(Map<Integer, List<int[]>> tenseStates, int[] quantityX, int[] quantityY) {
        var result = new QuantityValues();

        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            var coords = tenseStates.get(i+1);
            if (coords != null) {
                result.setValue(i, coords.size());
            }
        }
        return result;
    }

    static class QuantityValues extends ParamValuesBase {
        private final int[] values = new int[NUMBER_OF_STATES];

        public int[] getValues() {
            return values;
        }

        public int getValue(int state) {
            return values[state];
        }
        public void setValue(int state, int val) {
            values[state] = val;
        }

        public int getSum() {
            return Arrays.stream(getValues()).reduce(0, Integer::sum);
        }
    }

    protected void appendResultQuantity(String description, QuantityValues paramVal) {
        appendResultText(description + ":");
        for (int state = 0; state < NUMBER_OF_STATES; state++) {
            appendResultText(
                    String.format("%d:%17d", state+1, paramVal.getValue(state))
            );
        }
    }

    protected void appendResultParamValue(String paramName, int val) {
        appendResultText(String.format("%s: %9d", paramName, val));
    }
}
