package com.toy.weather.component;

import java.util.function.Supplier;

/**
 * Created by abhijitdc on 1/4/19.
 *
 * This class encapsulates a sensor which can give simulated measurement based on the Weather
 * condition provided. It accepts three functional interface to generate the simulated value.
 * This allow construction of different sensor with varied simulation functions.
 */
public class Sensor {
    private String sensorName;
    private Supplier<Double> sunnyFunc, rainyFunc, snowFunc;
    private SensorType sensorType;


    /**
     *
     * @param sensorName
     * @param sunnyFunc - java.util.function.Supplier to generate value when weather condition is "Sunny"
     * @param rainyFunc - java.util.function.Supplier to generate value when weather condition is "Rain"
     * @param snowFunc - java.util.function.Supplier to generate value when weather condition is "Snow"
     * @param sensorType - enum of sensor type to identify the sensor.
     */
    public Sensor(String sensorName, Supplier<Double> sunnyFunc, Supplier<Double> rainyFunc, Supplier<Double> snowFunc, SensorType sensorType) {
        this.sensorName = sensorName;
        this.sunnyFunc = sunnyFunc;
        this.rainyFunc = rainyFunc;
        this.snowFunc = snowFunc;
        this.sensorType = sensorType;
    }

    /**
     * Choose appropriate function based on Weather Condition (Sunny, Rain, Snow) and execute to get simulated measurement.
     * @param cond
     * @return
     * @throws Exception
     */
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