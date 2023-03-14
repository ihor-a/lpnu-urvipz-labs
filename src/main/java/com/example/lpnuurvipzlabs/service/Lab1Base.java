package com.example.lpnuurvipzlabs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class Lab1Base extends TextResultBase implements Lab1Service {
    static final int NUMBER_OF_STATES = 5;

    @Override
    public int getTargetState() {
        return 2; //start from 0
    }

    protected double[][] mCost = new double[][]{
            {22.5, 46.7, 65.5, 91.4, 109.2},
            {16.8, 31.6, 50.1, 65.9, 84.3},
            {15.8, 25.2, 36.8, 42.8, 53.2},
            {5.8, 13.9, 17.2, 25, 30.8}
    };

    protected int[][] mQuantity = new int[][]{
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1}
    };

    protected int[][] mzkK3K4ToP1 = new int[][] {
            {1, 1, 1, 2, 3},
            {1, 2, 2, 3, 3},
            {1, 2, 3, 4, 4},
            {1, 2, 3, 4, 4},
            {2, 3, 4, 4, 5}
    };
    protected Map<Integer, List<int[]>> mzkK3K4ToP1TenseStates = new HashMap<>() {{
        put(1, new ArrayList<>(List.of(new int[]{0, 0})));
        put(2, new ArrayList<>(List.of(new int[]{3, 0}, new int[]{1, 1}, new int[]{0, 4})));
        put(4, new ArrayList<>(List.of(new int[]{3, 2}, new int[]{2, 4})));
        put(5, new ArrayList<>(List.of(new int[]{4, 4})));
    }};;

    protected int[][] mzkK2P1ToP2 = new int[][] {
            {1, 2, 2, 3, 3},
            {1, 2, 3, 3, 4},
            {2, 2, 3, 4, 4},
            {2, 3, 4, 4, 5},
            {3, 3, 4, 5, 5}
    };
    protected Map<Integer, List<int[]>> mzkK2P1ToP2TenseStates = new HashMap<>() {{
        put(1, new ArrayList<>(List.of(new int[]{0, 0})));
        put(3, new ArrayList<>(List.of(new int[]{3, 0}, new int[]{2, 1}, new int[]{1, 3}, new int[]{0, 4})));
        put(5, new ArrayList<>(List.of(new int[]{4, 3}, new int[]{3, 4})));
    }};;

    protected int[][] mzkK1P2ToP3 = new int[][] {
            {1, 2, 3, 3, 4},
            {1, 2, 3, 4, 4},
            {2, 3, 3, 4, 5},
            {2, 3, 4, 4, 5},
            {3, 3, 4, 5, 5}
    };
    protected Map<Integer, List<int[]>> mzkK1P2ToP3TenseStates = new HashMap<>() {{
        put(3, new ArrayList<>(List.of(new int[]{2, 0}, new int[]{1, 2}, new int[]{0, 4})));
    }};;

    protected void appendResultParamValue(String paramName, double val) {
        appendResultText(String.format("%s: %20.3f", paramName, val));
    }

    abstract protected static class ParamValuesBase {
        private final int[] x = new int[NUMBER_OF_STATES];
        private final int[] y = new int[NUMBER_OF_STATES];

        public int getX(int state) {
            return x[state];
        }
        public void setX(int state, int val) {
            x[state] = val;
        }
        public int getY(int state) {
            return y[state];
        }
        public void setY(int state, int val) {
            y[state] = val;
        }
    }
}
