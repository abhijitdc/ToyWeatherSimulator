package com.toy.weather.driver;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.toy.weather.component.WeatherCondition;

/**
 * Created by abhijitdc on 1/4/19.
 *
 * Collection of probability vectors for use in Markov chain transition. Based on the zone of
 * the GeoLocation one of the probability vector will be chosen to generate simulated weather condition.
 * Each enum entry holds a 3X3 vector in one dimensional array.
 *
 */
public enum MarkovProbVector {

    Z1E1(1, 1, 0.6, 0.377, 0.023, 0.377, 0.6, 0.023, 0.58, 0.377, 0.043),
    Z1E2(1, 2, 0.34, 0.34, 0.32, 0.29, 0.34, 0.37, 0.34, 0.34, 0.32),
    Z1E3(1, 3, 0.025, 0.025, 0.95, 0.025, 0.027, 0.948, 0.023, 0.025, 0.952),
    Z2E1(2, 1, 0.59, 0.34, 0.07, 0.3, 0.5, 0.2, 0.4, 0.5, 0.1),
    Z2E2(2, 2, 0.18, 0.18, 0.64, 0.16, 0.2, 0.64, 0.1, 0.15, 0.75),
    Z2E3(2, 3, 0.025, 0.025, 0.95, 0.025, 0.033, 0.942, 0.02, 0.03, 0.95),
    Z3E1(3, 1, 0.18, 0.18, 0.64, 0.16, 0.21, 0.63, 0.11, 0.19, 0.7),
    Z3E2(3, 2, 0.025, 0.025, 0.95, 0.019, 0.029, 0.952, 0.01, 0.026, 0.964),
    Z3E3(3, 3, 0.015, 0.015, 0.97, 0.01, 0.023, 0.967, 0.005, 0.019, 0.976);

    int zoneNo;
    int elevRange;
    Double[] condProb;


    MarkovProbVector(int zoneNo, int elevRange, Double... condProb) {
        this.zoneNo = zoneNo;
        this.elevRange = elevRange;
        this.condProb = condProb;
    }

    //data structure to lookup a probability vector based on longitude zone and elevation
    public static final Table<Integer, Integer, MarkovProbVector> LOOKUP
            = HashBasedTable.create();

    static {
        for (MarkovProbVector probVector : values()) {
            LOOKUP.put(probVector.zoneNo, probVector.elevRange, probVector);
        }
    }

    /**
     * Markov chain transition function to determine the next likely weather condition based on the current.
     * @param currentWeatherCond
     * @return WeatherCondition
     */
    public WeatherCondition getNextWeatherCond(WeatherCondition currentWeatherCond) {
        int N = 3;
        double rand = Math.random();
        double sum = 0.0;
        int state = currentWeatherCond.getIndex();
        WeatherCondition resultCond;
        for (int i = 0; i < N; i++) {
            sum += condProb[(state * N) + i];
            if (rand <= sum) {
                state = i;
                break;
            }
        }
        return WeatherCondition.LOOKUP.get(state);
    }

}
