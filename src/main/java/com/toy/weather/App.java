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

public class App {

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


        Integer trainingLocationNum = inputJobject.get("trainingLocationNum") != null ? inputJobject.get("trainingLocationNum").getAsInt() : null;
        Integer trainingNumOfDays = inputJobject.get("trainingNumOfDays") != null ? inputJobject.get("trainingNumOfDays").getAsInt() : null;
        String trainingStartDate = inputJobject.get("trainingStartDate") != null ? inputJobject.get("trainingStartDate").getAsString() : null;

        Integer reportingLocationNum = inputJobject.get("reportingLocationNum") != null ? inputJobject.get("reportingLocationNum").getAsInt() : null;
        Integer reportingPerLocation = inputJobject.get("reportingPerLocation") != null ? inputJobject.get("reportingPerLocation").getAsInt() : null;
        String reportingStartDate = inputJobject.get("reportingStartDate") != null ? inputJobject.get("reportingStartDate").getAsString() : null;

        if (trainingLocationNum == null || trainingNumOfDays == null || Strings.isNullOrEmpty(trainingStartDate)
                || reportingLocationNum == null || reportingPerLocation == null || Strings.isNullOrEmpty(reportingStartDate)) {
            System.out.println("Invalid Input");
            System.exit(1);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime traingStartDt = LocalDateTime.parse(trainingStartDate, dateTimeFormatter);
        LocalDateTime reportingStartDt = LocalDateTime.parse(reportingStartDate, dateTimeFormatter);


        System.out.println(" Input Params >>> " + inputJobject);

        try {

            System.out.println("Star training data generation >>>>>>>> ");
            TrainingDataGenerator tdGen = new TrainingDataGenerator(traingStartDt, trainingLocationNum, trainingNumOfDays, RUNID);
            tdGen.generateTraingData();
            System.out.println("End training data generation  >>>>>>>> ");

            System.out.println("Star training model >>>>>>>> ");
            RegressionModelTrainer modelTrainer = new RegressionModelTrainer(RUNID);
            for (SensorType st : SensorType.values()) {
                System.out.println("Start training model for " + st);
                modelTrainer.trainModel(st.getSensorName() + "RegressionModel", st);
            }

            System.out.println("Start training model for Weather Condition");
            WeatherCondClassifierTrainer classifierTrainer = new WeatherCondClassifierTrainer(RUNID);
            classifierTrainer.trainModel();
            System.out.println("End training model >>>>>>>> ");

            System.out.println("Start simulation  >>>>>>>> ");
            new SampleGenerator(RUNID).generateSamples(reportingStartDt, reportingLocationNum, reportingPerLocation);
            System.out.println("End simulation  >>>>>>>> ");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
