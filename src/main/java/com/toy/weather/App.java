package com.toy.weather;


import com.toy.weather.component.SensorType;
import com.toy.weather.models.RegressionModelTrainer;
import com.toy.weather.models.WeatherCondClassifierTrainer;
import com.toy.weather.process.TrainingDataGenerator;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {

        TrainingDataGenerator tdGen = null;
        try {
            tdGen = new TrainingDataGenerator(LocalDateTime.of(2018, 1, 1, 0, 0, 0), 1000, 365);
            tdGen.generateTraingData();

            RegressionModelTrainer modelTrainer = new RegressionModelTrainer();
            WeatherCondClassifierTrainer classifierTrainer = new WeatherCondClassifierTrainer();

            for (SensorType st : SensorType.values())
                modelTrainer.trainModel(st + "RegressionModel", st);

            classifierTrainer.trainModel("weatherConditionClassifierModel");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
