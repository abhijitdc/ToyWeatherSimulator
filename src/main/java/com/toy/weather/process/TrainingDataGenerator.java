package com.toy.weather.process;

import com.toy.weather.component.GeoLocation;
import com.toy.weather.component.Sensor;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created by abhijitdc on 1/4/19.
 */
public class TrainingDataGenerator {

    private LocalDateTime startDate;
    private int noOfGeoLocations;
    private int noOfDays;
    private NormalDistribution nd = new NormalDistribution();

    public TrainingDataGenerator(LocalDateTime startDate, int noOfGeoLocations, int noOfDays) {
        this.startDate = startDate;
        this.noOfGeoLocations = noOfGeoLocations;
        this.noOfDays = noOfDays;
    }

    private Sensor tempSensor = new Sensor("TEMPERATURE",
            () -> 3.0 + 5.0 * nd.sample(),
            () -> 20.5 + 5.0 * nd.sample(),
            () -> -10.0 + 0.5 * nd.sample());

    private Sensor humiditySensor = new Sensor("HUMIDITY",
            () -> (70.0 - 30.0) * nd.sample() + 30.0,
            () -> (95.0 - 70.0) * nd.sample() + 70.0,
            () -> (70.0 - 40.0) * nd.sample() + 40.0);

    private Sensor pressureSensor = new Sensor("PRESSURE",
            () -> 700.0 + 0.2 * nd.sample(),
            () -> 900.0 + 1.5 * nd.sample(),
            () -> 800.0 + 2.0 * nd.sample());

    List<GeoLocation> lcList = Arrays.asList(
            new GeoLocation(-86.67, 151.21, 39),
            new GeoLocation(16.67, 11.21, 189));

    public void generateWeatherConditionData() {

    }
}
