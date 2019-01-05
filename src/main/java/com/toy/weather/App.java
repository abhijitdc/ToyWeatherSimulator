package com.toy.weather;


import com.toy.weather.component.Sensor;
import org.apache.commons.math3.distribution.NormalDistribution;

public class App {
    public static void main(String[] args) {

        NormalDistribution nd = new NormalDistribution();

        Sensor tempSensor = new Sensor("TEMPERATURE",
                () -> 3.0 + 5.0 * nd.sample(),
                () -> 20.5 + 5.0 * nd.sample(),
                () -> -10.0 + 0.5 * nd.sample());

        Sensor humiditySensor = new Sensor("HUMIDITY",
                () -> (70.0 - 30.0) * nd.sample() + 30.0,
                () -> (95.0 - 70.0) * nd.sample() + 70.0,
                () -> (70.0 - 40.0) * nd.sample() + 40.0);

        Sensor pressureSensor = new Sensor("PRESSURE",
                () -> 700.0 + 0.2 * nd.sample(),
                () -> 900.0 + 1.5 * nd.sample(),
                () -> 800.0 + 2.0 * nd.sample());



    }
}
