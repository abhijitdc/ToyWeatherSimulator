package com.toy.weather.models;

import java.util.HashMap;
import java.util.Map;

import com.toy.weather.component.SensorType;
import com.toy.weather.component.WeatherCondition;
import org.apache.spark.mllib.linalg.Vectors;
import scala.Tuple2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class WeatherCondClassifierTrainer {

    public void trainModel(String modelName) {
        SparkConf sparkConf = new SparkConf().setMaster("local[*]").setAppName("WeatherCondClassifierTrainer");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        // Load and parse the data file.
        String datapath = "src/main/resources/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();
        JavaRDD<LabeledPoint> data = allData.filter(l -> l.features().toArray()[4] == 0.0);
        data.take(10).forEach(l -> System.out.println(l.features().toArray()[3]));

        // Split the data into training and test sets (30% held out for testing)
        JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
        JavaRDD<LabeledPoint> trainingData = splits[0];
        JavaRDD<LabeledPoint> testData = splits[1];

        // Train a RandomForest model.
        // Empty categoricalFeaturesInfo indicates all features are continuous.
        int numClasses = WeatherCondition.values().length;
        Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
        Integer numTrees = 3; // Use more in practice.
        String featureSubsetStrategy = "auto"; // Let the algorithm choose.
        String impurity = "gini";
        int maxDepth = 4;
        int maxBins = 32;
        int seed = 12345;

        RandomForestModel model = RandomForest.trainClassifier(trainingData, numClasses,
                categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins,
                seed);

        // Evaluate model on test instances and compute test error
        JavaPairRDD<Double, Double> predictionAndLabel =
                testData.mapToPair(p -> new Tuple2(model.predict(p.features()), p.label()));
        double testErr =
                predictionAndLabel.filter(pl -> !pl._1().equals(pl._2())).count() / (double) testData.count();
        System.out.println("Test Error: " + testErr);
        System.out.println("Learned classification forest model:\n" + model.toDebugString());

        // Save and load model
        model.save(jsc.sc(), "target/tmp/" + modelName);
        RandomForestModel sameModel = RandomForestModel.load(jsc.sc(),
                "target/tmp/" + modelName);
        double val = sameModel.predict(Vectors.dense(34.21, 12.31, 54.0, 65.0, 0.0));
        System.out.println("<<<<<<<<<<<<<<<<<<<<< WeatherCond Predic >>>>>>>>>>>>>>>>>>>>>>>>>>>> " + val);

        jsc.stop();
    }


}
