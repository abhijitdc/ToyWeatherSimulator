package com.toy.weather.model;

import com.toy.weather.component.SensorType;
import com.toy.weather.models.RegressionModelTrainer;
import com.toy.weather.process.TrainingDataGenerator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by abhijitdc on 1/6/19.
 */
public class RegressionModelTrainerTest {

    private static final int RUNID = 9999;

    @Before
    public void generateTraingData() throws Exception {
        TrainingDataGenerator tdg = new TrainingDataGenerator(LocalDateTime.of(2019,1,4,0,0,0),1,1,RUNID);
        tdg.generateTraingData();
    }

    @Test
    public void testRegressionModel(){
        RegressionModelTrainer regTra = new RegressionModelTrainer(RUNID);
        RandomForestModel model = regTra.trainModel("testModel", SensorType.TEMPSENSOR);
        Assert.assertTrue(model instanceof RandomForestModel);
    }
}
