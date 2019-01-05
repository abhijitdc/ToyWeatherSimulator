package com.toy.weather.component;

import java.util.function.Supplier;

/**
 * Created by abhijitdc on 1/4/19.
 */
public class Sensor {
    private String sensorName;
    private Supplier<Double> sunnyFunc, rainyFunc, snowFunc;
    private SensorType sensorType;


    public String getSensorName() {
        return sensorName;
    }

    public Sensor(String sensorName, Supplier<Double> sunnyFunc, Supplier<Double> rainyFunc, Supplier<Double> snowFunc, SensorType sensorType) {
        this.sensorName = sensorName;
        this.sunnyFunc = sunnyFunc;
        this.rainyFunc = rainyFunc;
        this.snowFunc = snowFunc;
        this.sensorType = sensorType;
    }

    public Double getSensorData(WeatherCondition cond) throws Exception {
        double observation;
        switch (cond) {
            case SUNNY:
                observation = sunnyFunc.get();
                break;
            case RAIN:
                observation = rainyFunc.get();
                break;
            case SNOW:
                observation = snowFunc.get();
                break;
            default:
                throw new Exception("Unknown Weather Condition");

        }
        return observation;
    }
}