package com.toy.weather.driver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.toy.weather.component.WeatherCondition;

/**
 * Created by abhijitdc on 1/4/19.
 */
public enum MarkovProbVector {

    Z1E1(1, 1, 0.4985, 0.4985, 0.003),
    Z1E2(1, 2, 0.475, 0.475, 0.05),
    Z1E3(1, 3, 0.34, 0.34, 0.32),
    Z2E1(2, 1, 0.475, 0.475, 0.05),
    Z2E2(2, 2, 0.34, 0.34, 0.32),
    Z2E3(2, 3, 0.025, 0.025, 0.95),
    Z3E1(3, 1, 0.16, 0.16, 0.68),
    Z3E2(3, 2, 0.025, 0.025, 0.95),
    Z3E3(3, 3, 0.015, 0.015, 0.97);

    int zoneNo;

    public int getZoneNo() {
        return zoneNo;
    }

    public int getElevRange() {
        return elevRange;
    }

    public Double[] getCondProb() {
        return condProb;
    }

    int elevRange;
    Double[] condProb;

    MarkovProbVector(int zoneNo, int elevRange, Double... condProb) {
        this.zoneNo = zoneNo;
        this.elevRange = elevRange;
        this.condProb = condProb;
    }

    public static final Table<Integer, Integer, MarkovProbVector> LOOKUP
            = HashBasedTable.create();

    static {
        for (MarkovProbVector probVector : values()) {
            LOOKUP.put(probVector.zoneNo, probVector.elevRange, probVector);
        }
    }

    public WeatherCondition getNextWeatherCond(WeatherCondition currentWeatherCond) {
        int N = 3;
        double rand = Math.random();
        double sum = 0.0;
        int state = currentWeatherCond.getIndex();
        WeatherCondition resultCond;
        for (int i = 0; i < N; i++) {
            sum += condProb[i];
            if (rand <= sum) {
                state = i;
                break;
            }
        }
        return WeatherCondition.LOOKUP.get(state);
    }

}
