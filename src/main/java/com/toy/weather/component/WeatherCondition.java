package com.toy.weather.component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abhijitdc on 1/4/19.
 */
public enum WeatherCondition {

    SUNNY("SUNNY", 0),
    RAIN("RAIN", 1),
    SNOW("SNOW", 2);

    private String condName;
    private int index;

    public String getCondName() {
        return condName;
    }

    public int getIndex() {
        return index;
    }


    public static final Map<Integer, WeatherCondition> LOOKUP = new HashMap<>();

    static {
        for (WeatherCondition wc : values())
            LOOKUP.put(wc.index, wc);
    }

    WeatherCondition(String condName, int index) {
        this.condName = condName;
        this.index = index;
    }
}
