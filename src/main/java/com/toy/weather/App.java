package com.toy.weather;


import com.toy.weather.component.SensorType;
import com.toy.weather.models.RegressionModelTrainer;
import com.toy.weather.models.WeatherCondClassifierTrainer;
import com.toy.weather.process.SampleGenerator;
import com.toy.weather.process.TrainingDataGenerator;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {

        TrainingDataGenerator tdGen = null;
        long RUNID = System.currentTimeMillis();

        try {
            tdGen = new TrainingDataGenerator(LocalDateTime.of(2018, 1, 1, 0, 0, 0), 1000, 365, RUNID);
            tdGen.generateTraingData();

            RegressionModelTrainer modelTrainer = new RegressionModelTrainer(RUNID);
            for (SensorType st : SensorType.values())
                modelTrainer.trainModel(st.getSensorName() + "RegressionModel", st);

            WeatherCondClassifierTrainer classifierTrainer = new WeatherCondClassifierTrainer(RUNID);
            classifierTrainer.trainModel();

            new SampleGenerator(RUNID).generateSamples(LocalDateTime.of(2016, 1, 1, 0, 0, 0), 10, 10 );

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
