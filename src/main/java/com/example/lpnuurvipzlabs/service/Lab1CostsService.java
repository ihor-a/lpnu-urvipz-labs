package com.example.lpnuurvipzlabs.service;

import java.util.*;

public class Lab1CostsService extends Lab1Base {

    protected CostsValues mP1Cost, mP2Cost, mP3Cost;

    public String calculate() {
        resetResult();

        mP1Cost = makeMinimalCosts(mzkK3K4ToP1TenseStates, mCost[2], mCost[3]);
        appendResultCosts("Minimal Costs of P1 indicator by affected states", mP1Cost);
        appendResultNewline();

        mP2Cost = makeMinimalCosts(mzkK2P1ToP2TenseStates, mCost[1], mP1Cost.getValues());
        appendResultCosts("Minimal Costs of P2 indicator by affected states", mP2Cost);
        appendResultNewline();

        mP3Cost = makeMinimalCosts(mzkK1P2ToP3TenseStates, mCost[0], mP2Cost.getValues());
        appendResultCosts("Minimal Costs of P3 indicator by affected states", mP3Cost);
        appendResultNewline();

        appendResultText("We're going to define all states in our tree..");
        var reqK1State = mP3Cost.getX(getTargetState());
        var reqP2State = mP3Cost.getY(getTargetState());
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

        appendResultText(String.format("Final minimal costs for state %d are:", getTargetState()+1));
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
                    if (result.getValue(i) == 0.0 || sum < result.getValue(i)) {
                        result.setValue(i, sum);
                        result.setX(i, coordinate[0]);
                        result.setY(i, coordinate[1]);
                    }
                }
            }
        }
        return result;
    }

    static class CostsValues extends ParamValuesBase {
        private final double[] values = new double[NUMBER_OF_STATES];

        public double[] getValues() {
            return values;
        }

        public double getValue(int state) {
            return values[state];
        }
        public void setValue(int state, double val) {
            values[state] = val;
        }
    }

    protected void appendResultCosts(String description, CostsValues paramVal) {
        appendResultText(description + ":");
        for (int state = 0; state < NUMBER_OF_STATES; state++) {
            if (paramVal.getValue(state) == 0) {
                continue;
            }
            appendResultText(
                    String.format("%d:%20.3f\t(%d, %d)", state+1, paramVal.getValue(state), paramVal.getX(state)+1, paramVal.getY(state)+1)
            );
        }
    }

    protected void appendResultReqiredState(String paramName, int state) {
        appendResultText(String.format("Required %s state:%10d", paramName, state+1));
    }
}
