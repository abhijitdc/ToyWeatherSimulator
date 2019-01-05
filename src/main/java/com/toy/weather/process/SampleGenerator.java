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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class SampleGenerator {

    private long RUNID;

    public SampleGenerator(long RUNID) throws InstantiationException {

        this.RUNID = RUNID;
    }

    public void generateSampleForLocation(List<GeoLocation> sampleLocations, LocalDateTime observationStartDate, int noOfSamplesPerLocation) throws Exception {
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
        String datapath = "target/tmp/" + RUNID + "/sampledata.dat";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(datapath, false))) {
            int locationNo = 0;
            for (GeoLocation glc : sampleLocations) {
                System.out.println("Location " + glc);
                for (int i = 0; i < noOfSamplesPerLocation; i++) {
                    LocalDateTime sampleDate = observationStartDate.plusDays(rd.nextInt(5 * 365));
                    double weatheCondIndex = weatherCondModel.predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), 0.0));
                    double temperature = sensorModels.get(SensorType.TEMPSENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.TEMPSENSOR.getSensorId()));
                    double humidity = sensorModels.get(SensorType.HUMIDSENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.HUMIDSENSOR.getSensorId()));
                    double pressure = sensorModels.get(SensorType.PRESSURESENSOR).predict(Vectors.dense(glc.getLongi(), glc.getLati(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.PRESSURESENSOR.getSensorId()));

                    String strSampleDate = sampleDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US));
                    WeatherCondition wc = WeatherCondition.LOOKUP.get((int) Math.round(weatheCondIndex));
                    String sampleData = String.format("%d|%.3f,%.3f,%d|%s|%s|%.2f|%.2f|%d", locationNo, glc.getLati(), glc.getLati(), glc.getElv(), strSampleDate, wc.getCondName(), temperature, humidity, (int) Math.round(pressure));
                    System.out.println(sampleData);
                    bw.write(sampleData);
                    bw.newLine();
                }
                locationNo++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to generate sample data");
        } finally {
            jsc.stop();
        }
    }

    public void generateSamples(LocalDateTime observationStartDate, int noOfLocations, int noOfSamplesPerLocation) throws Exception {
        List<GeoLocation> sampleLocations = new LocationSampleGenerator().samples(noOfLocations);
        generateSampleForLocation(sampleLocations, observationStartDate, noOfSamplesPerLocation);
    }

    public static void main(String rgs[]) throws Exception {
        SampleGenerator sg = new SampleGenerator(1546727549623L);
        sg.generateSamples(LocalDateTime.of(2016, 1, 1, 0, 0, 0), 10, 10);
    }
}
