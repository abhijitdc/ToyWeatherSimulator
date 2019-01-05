package com.toy.weather.process;

import com.toy.weather.component.GeoLocation;
import com.toy.weather.component.Sensor;
import com.toy.weather.component.SensorType;
import com.toy.weather.component.WeatherCondition;
import com.toy.weather.models.WeatherCondClassifierTrainer;
import com.toy.weather.util.LocationSampleGenerator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class SampleGenerator {

    private List<GeoLocation> sampleLocations;
    private LocalDateTime observationStartDate;
    private int noOfLocations;
    private int noOfSamplesPerLocation;
    private long RUNID;

    public SampleGenerator(LocalDateTime observationStartDate, int noOfLocations, int noOfSamplesPerLocation, long RUNID) throws InstantiationException {
        try {
            this.observationStartDate = observationStartDate;
            this.noOfLocations = noOfLocations;
            this.noOfSamplesPerLocation = noOfSamplesPerLocation;
            this.sampleLocations = new LocationSampleGenerator().samples(noOfLocations);
            this.RUNID = RUNID;
        } catch (IOException e) {
            e.printStackTrace();
            throw new InstantiationException();
        }
    }

    public void generateSamples() {
        Random rd = new Random();
        Map<SensorType, RandomForestModel> sensorModels = new HashMap<>();
        SparkConf sparkConf = new SparkConf().setMaster("local[*]").setAppName("modelExecutor");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        RandomForestModel weatherCondModel = RandomForestModel.load(jsc.sc(),
                "target/tmp/" + RUNID + "/" + WeatherCondClassifierTrainer.modelName);

        for (SensorType st : SensorType.values()) {
            RandomForestModel regressionModel = RandomForestModel.load(jsc.sc(),
                    "target/tmp/" + RUNID + "/" + st.getSensorName() + "RegressionModel");
            sensorModels.put(st, regressionModel);
        }

        int locationNo = 0;
        for (GeoLocation glc : sampleLocations) {
            for (int i = 0; i < noOfSamplesPerLocation; i++) {
                LocalDateTime sampleDate = observationStartDate.plusDays(rd.nextInt(5 * 365));
                double weatheCondIndex = weatherCondModel.predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), 0.0));
                double temperature = sensorModels.get(SensorType.TEMPSENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.TEMPSENSOR.getSensorId()));
                double humidity = sensorModels.get(SensorType.HUMIDSENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.HUMIDSENSOR.getSensorId()));
                double pressure = sensorModels.get(SensorType.PRESSURESENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.PRESSURESENSOR.getSensorId()));
                System.out.println("Location " + glc);
                WeatherCondition wc = WeatherCondition.LOOKUP.get((int) Math.round(weatheCondIndex));
                System.out.println(String.format("COND %s TEMP %.2f HUMID %.2f PRESS %.2f Date %s", wc.getCondName(), temperature, humidity, pressure, sampleDate));
            }
            locationNo++;
        }
    }
}
