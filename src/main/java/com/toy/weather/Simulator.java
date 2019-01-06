package com.toy.weather;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.toy.weather.component.SensorType;
import com.toy.weather.models.RegressionModelTrainer;
import com.toy.weather.models.WeatherCondClassifierTrainer;
import com.toy.weather.process.SampleGenerator;
import com.toy.weather.process.TrainingDataGenerator;
import org.spark_project.guava.base.Strings;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Simulator {

    private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) throws FileNotFoundException {

        long RUNID = System.currentTimeMillis();
        System.out.println("RUNID >>>>>>>>>> " + RUNID);

        String jsonInput = "src/main/resources/input.json";


        if (args.length == 1) {
            jsonInput = args[0];
        } else {
            System.out.println("No input provided, using default input json file ");
        }

        JsonElement jelement = new JsonParser().parse(new FileReader(jsonInput));
        JsonObject inputJobject = jelement.getAsJsonObject();


        Integer reportingLocationNum = inputJobject.get("reportingLocationNum") != null ? inputJobject.get("reportingLocationNum").getAsInt() : null;
        Integer reportingPerLocation = inputJobject.get("reportingPerLocation") != null ? inputJobject.get("reportingPerLocation").getAsInt() : null;
        String reportingStartDate = inputJobject.get("reportingStartDate") != null ? inputJobject.get("reportingStartDate").getAsString() : null;

        if (reportingLocationNum == null || reportingPerLocation == null || Strings.isNullOrEmpty(reportingStartDate)) {
            System.out.println("Invalid Input");
            System.exit(1);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime traingStartDt = LocalDateTime.parse(reportingStartDate, dateTimeFormatter);


        System.out.println(" Input Params >>> " + inputJobject);

        try {

            System.out.println("Star simulator data generation >>>>>>>> ");
            TrainingDataGenerator tdGen = new TrainingDataGenerator(traingStartDt, reportingLocationNum, reportingPerLocation, RUNID);
            tdGen.generateSimulationData();
            System.out.println("End simulator data generation  >>>>>>>> ");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
