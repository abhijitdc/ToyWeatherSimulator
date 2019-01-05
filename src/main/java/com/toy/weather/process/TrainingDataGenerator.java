package com.toy.weather.process;

import com.toy.weather.component.GeoLocation;
import com.toy.weather.component.Sensor;
import com.toy.weather.component.SensorType;
import com.toy.weather.component.WeatherCondition;
import com.toy.weather.util.LocationSampleGenerator;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by abhijitdc on 1/4/19.
 */
public class TrainingDataGenerator {

    private LocalDateTime startDate;
    private int noOfDays;
    private NormalDistribution nd = new NormalDistribution();
    private List<GeoLocation> sampleLocations;
    private int noOfGeoLocations;
    private Map<SensorType, Sensor> sensorCollection;

    public TrainingDataGenerator(LocalDateTime startDate, int noOfGeoLocations, int noOfDays) throws InstantiationException {
        this.startDate = startDate;
        this.noOfDays = noOfDays;
        this.noOfGeoLocations = noOfGeoLocations;
        init();
    }

    private void init() throws InstantiationException {
        try {
            this.sampleLocations = new LocationSampleGenerator().samples(noOfGeoLocations);
            sensorCollection = new HashMap<>();

            Sensor tempSensor = new Sensor("TEMPERATURE",
                    () -> 3.0 + 5.0 * nd.sample(),
                    () -> 20.5 + 5.0 * nd.sample(),
                    () -> -10.0 + 0.5 * nd.sample(), SensorType.TEMPSENSOR);

            Sensor humiditySensor = new Sensor("HUMIDITY",
                    () -> (70.0 - 30.0) * nd.sample() + 30.0,
                    () -> (95.0 - 70.0) * nd.sample() + 70.0,
                    () -> (70.0 - 40.0) * nd.sample() + 40.0, SensorType.HUMIDSENSOR);

            Sensor pressureSensor = new Sensor("PRESSURE",
                    () -> 700.0 + 0.2 * nd.sample(),
                    () -> 900.0 + 1.5 * nd.sample(),
                    () -> 800.0 + 2.0 * nd.sample(), SensorType.PRESSURESENSOR);

            sensorCollection.put(SensorType.TEMPSENSOR, tempSensor);
            sensorCollection.put(SensorType.HUMIDSENSOR, humiditySensor);
            sensorCollection.put(SensorType.PRESSURESENSOR, pressureSensor);

        } catch (IOException e) {
            throw new InstantiationException("Failed to get GeoLocation samples");
        }
    }


    public void generateTraingData() throws Exception {

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("src/main/resources/training.dat"))))) {
            for (GeoLocation gcl : sampleLocations) {
                //select a random starting weather condition for the geo location
                WeatherCondition wCond = WeatherCondition.LOOKUP.get(new Random().nextInt(3));
                LocalDateTime sampleDate = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startDate.getHour(), startDate.getMinute(), startDate.getSecond());

                for (int i = 0; i < noOfDays; i++) {

                    wCond = gcl.getMarkovProbVector().getNextWeatherCond(wCond);

                    {
                        String dataSample = String.format("%d 1:%.2f 2:%.2f 3:%d 4:%d 5:%s", wCond.getIndex(), gcl.getLongi(), gcl.getLati(), gcl.getElv(), sampleDate.getDayOfYear(), 0);
                        bw.write(dataSample);
                        bw.newLine();
                    }

                    for(SensorType st: SensorType.values())
                    {
                        Sensor sensor = sensorCollection.get(st);
                        Double temperature = sensor.getSensorData(wCond);
                        String dataSample = String.format("%.2f 1:%.2f 2:%.2f 3:%d 4:%d 5:%d", temperature, gcl.getLongi(), gcl.getLati(), gcl.getElv(), sampleDate.getDayOfYear(), st.getSensorId());
                        bw.write(dataSample);
                        bw.newLine();
                    }

                    sampleDate = sampleDate.plusDays(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to generate training data");
        }

    }


}
