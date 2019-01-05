package com.toy.weather;


import com.toy.weather.process.TrainingDataGenerator;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {

        TrainingDataGenerator tdGen = null;
        try {
            tdGen = new TrainingDataGenerator(LocalDateTime.of(2018, 1, 1, 0, 0, 0), 10000, 365);
            tdGen.generateTraingData();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
