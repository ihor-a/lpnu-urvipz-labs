package com.example.lpnuurvipzlabs.service;

import java.util.*;

public class Lab1Service extends TextResultBase {

    static final int NUMBER_OF_STATES = 5;
    final int TARGET_STATE = 2; //start from 0

    private double[][] mCost = new double[][]{
            {22.5, 46.7, 65.5, 91.4, 109.2},
            {16.8, 31.6, 50.1, 65.9, 84.3},
            {15.8, 25.2, 36.8, 42.8, 53.2},
            {5.8, 13.9, 17.2, 25, 30.8}
    };

    private int[][] mzkK3K4ToP1 = new int[][] {
            {1, 1, 1, 2, 3},
            {1, 2, 2, 3, 3},
            {1, 2, 3, 4, 4},
            {1, 2, 3, 4, 4},
            {2, 3, 4, 4, 5}
    };
    private Map<Integer, List<int[]>> mzkK3K4ToP1TenseStates = new HashMap<>() {{
        put(1, new ArrayList<>(List.of(new int[]{0, 0})));
        put(2, new ArrayList<>(List.of(new int[]{3, 0}, new int[]{1, 1}, new int[]{0, 4})));
        put(4, new ArrayList<>(List.of(new int[]{3, 2}, new int[]{2, 4})));
        put(5, new ArrayList<>(List.of(new int[]{4, 4})));
    }};;
    private CostsValues mP1Cost;
    private int[][] mP1Count;

    private int[][] mzkK2P1ToP2 = new int[][] {
            {1, 2, 2, 3, 3},
            {1, 2, 3, 3, 4},
            {2, 2, 3, 4, 4},
            {2, 3, 4, 4, 5},
            {3, 3, 4, 5, 5}
    };
    private Map<Integer, List<int[]>> mzkK2P1ToP2TenseStates = new HashMap<>() {{
        put(1, new ArrayList<>(List.of(new int[]{0, 0})));
        put(3, new ArrayList<>(List.of(new int[]{3, 0}, new int[]{2, 1}, new int[]{1, 3}, new int[]{0, 4})));
        put(5, new ArrayList<>(List.of(new int[]{4, 3}, new int[]{3, 4})));
    }};;
    private CostsValues mP2Cost;
    private int[][] mP2Count;

    private int[][] mzkK1P2ToP3 = new int[][] {
            {1, 2, 3, 3, 4},
            {1, 2, 3, 4, 4},
            {2, 3, 3, 4, 5},
            {2, 3, 4, 4, 5},
            {3, 3, 4, 5, 5}
    };
    private Map<Integer, List<int[]>> mzkK1P2ToP3TenseStates = new HashMap<>() {{
        put(3, new ArrayList<>(List.of(new int[]{2, 0}, new int[]{1, 2}, new int[]{0, 4})));
    }};;
    private CostsValues mP3Cost;
    private int[][] mP3Count;

    public String calculate() {
        resetResult();

        mP1Cost = makeMinimalCosts(mzkK3K4ToP1TenseStates, mCost[2], mCost[3]);
        appendResultCosts("Minimal Costs of P1 indicator by affected states", mP1Cost);
        appendResultNewline();

        mP2Cost = makeMinimalCosts(mzkK2P1ToP2TenseStates, mCost[1], mP1Cost.getCosts());
        appendResultCosts("Minimal Costs of P2 indicator by affected states", mP2Cost);
        appendResultNewline();

        mP3Cost = makeMinimalCosts(mzkK1P2ToP3TenseStates, mCost[0], mP2Cost.getCosts());
        appendResultCosts("Minimal Costs of P3 indicator by affected states", mP3Cost);
        appendResultNewline();

        appendResultText("We're going to define all states in our tree..");
        var reqK1State = mP3Cost.getX(TARGET_STATE);
        var reqP2State = mP3Cost.getY(TARGET_STATE);
        var reqK2State = mP2Cost.getX(reqP2State);
        var reqP1State = mP2Cost.getY(reqP2State);
        var reqK3State = mP1Cost.getX(reqP1State);
        var reqK4State = mP1Cost.getY(reqP1State);
        appendResultReqiredState("K1", reqK1State);
        appendResultReqiredState("P2", reqP2State);
        appendResultReqiredState("K2", reqK2State);
        appendResultReqiredState("P1", reqP1State);
        appendResultReqiredState("K3", reqK3State);
        appendResultReqiredState("K4", reqK4State);
        appendResultNewline();

        appendResultText(String.format("Final minimal costs for state %d are:", TARGET_STATE+1));
        appendResultParamValue("K1", mCost[0][reqK1State]);
        appendResultParamValue("K2", mCost[1][reqK2State]);
        appendResultParamValue("K3", mCost[2][reqK3State]);
        appendResultParamValue("K4", mCost[3][reqK4State]);

        return getResult();
    }

    private CostsValues makeMinimalCosts(Map<Integer, List<int[]>> tenseStates, double[] costX, double[] costY) {
        var result = new CostsValues();
        double sum = 0.0;

        for (int i = 0; i < NUMBER_OF_STATES; i++) {
            List<Double> costs = new ArrayList<>();
            var coords = tenseStates.get(i+1);
            if (coords != null) {
                for (int[] coordinate: coords) {
                    sum = costX[coordinate[0]] + costY[coordinate[1]];
                    if (result.getCosts(i) == 0.0 || sum < result.getCosts(i)) {
                        result.setCosts(i, sum);
                        result.setX(i, coordinate[0]);
                        result.setY(i, coordinate[1]);
                    }
                }
            }
        }
        return result;
    }

    static class CostsValues {
        private final double[] costs = new double[NUMBER_OF_STATES];
        private final int[] x = new int[NUMBER_OF_STATES];
        private final int[] y = new int[NUMBER_OF_STATES];

        public double[] getCosts() {
            return costs;
        }

        public double getCosts(int state) {
            return costs[state];
        }
        public void setCosts(int state, double val) {
            costs[state] = val;
        }
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

    protected void appendResultCosts(String description, Lab1Service.CostsValues costsVal) {
        appendResultText(description + ":");
        for (int state = 0; state < Lab1Service.NUMBER_OF_STATES; state++) {
            if (costsVal.getCosts(state) == 0) {
                continue;
            }
            appendResultText(
                    String.format("%d:\t%.3f\t\t(%d, %d)", state+1, costsVal.getCosts(state), costsVal.getX(state)+1, costsVal.getY(state)+1)
            );
        }
    }

    protected void appendResultReqiredState(String paramName, int state) {
        appendResultText(String.format("Required %s state: %d", paramName, state+1));
    }

    protected void appendResultParamValue(String paramName, double val) {
        appendResultText(String.format("%s: %.3f", paramName, val));
    }
}
