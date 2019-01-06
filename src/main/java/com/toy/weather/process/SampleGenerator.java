package com.toy.weather.process;

import com.toy.weather.component.GeoLocation;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by abhijitdc on 1/5/19.
 * <p>
 * Ths class will generate the simulated weather data based on the classification and regression models.
 * The generated data will be stored in file under target/tmp/RUNID/sampledata.dat.
 * RUNID helps keep all training/predicted data and model organized in one directory.
 */
public class SampleGenerator {

    private long RUNID;

    public SampleGenerator(long RUNID) throws InstantiationException {

        this.RUNID = RUNID;
    }

    /**
     * This method will generate simulated weather data for a given set of locations.
     *
     * @param sampleLocations        - set of locations to generate simulated weather data based on models.
     * @param observationStartDate   - Start date for the observation
     * @param noOfSamplesPerLocation - number of observations per locations
     * @throws Exception
     */
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
                for (int i = 0; i < noOfSamplesPerLocation; i++) {
                    LocalDateTime sampleDate = observationStartDate.plusDays(rd.nextInt(5 * 365));

                    double weatheCondIndex = weatherCondModel.predict(Vectors.dense(glc.getLati(), glc.getLongi(), glc.getElv(), sampleDate.getDayOfYear(), 0.0));
                    double temperature = sensorModels.get(SensorType.TEMPSENSOR).predict(Vectors.dense(glc.getLati(), glc.getLongi(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.TEMPSENSOR.getSensorId()));
                    double humidity = sensorModels.get(SensorType.HUMIDSENSOR).predict(Vectors.dense(glc.getLati(), glc.getLongi(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.HUMIDSENSOR.getSensorId()));
                    double pressure = sensorModels.get(SensorType.PRESSURESENSOR).predict(Vectors.dense(glc.getLati(), glc.getLongi(), glc.getElv(), sampleDate.getDayOfYear(), SensorType.PRESSURESENSOR.getSensorId()));

                    String strSampleDate = sampleDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US));
                    WeatherCondition wc = WeatherCondition.LOOKUP.get((int) Math.round(weatheCondIndex));
                    String sampleData = String.format("%d|%.3f,%.3f,%d|%s|%s|%.2f|%.2f|%d", locationNo, glc.getLati(), glc.getLongi(), glc.getElv(), strSampleDate, wc.getCondName(), temperature, humidity, (int) Math.round(pressure));
//                    System.out.println(sampleData);
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

    /**
     * This method will randomly select locations from the elevation_DE.BMP file to get
     * real world coordinates and elevation data.
     *
     * @param observationStartDate   - Start date for the observation
     * @param noOfLocations          - number of locations for which the models will generate simulated data
     * @param noOfSamplesPerLocation - number of samples per location
     * @throws Exception
     */
    public void generateSamples(LocalDateTime observationStartDate, int noOfLocations, int noOfSamplesPerLocation) throws Exception {
        List<GeoLocation> sampleLocations = new LocationSampleGenerator().samples(noOfLocations);
        generateSampleForLocation(sampleLocations, observationStartDate, noOfSamplesPerLocation);
    }

}
