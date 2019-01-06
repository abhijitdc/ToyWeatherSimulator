package com.toy.weather.process;

import com.toy.weather.component.SensorType;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class TraingDataGeneratorTest {
    private static final int RUNID = 9999;
    private JavaSparkContext sparkCtx;

    @Before
    public void generateTraingData() throws Exception {
        TrainingDataGenerator tdg = new TrainingDataGenerator(LocalDateTime.of(2019, 1, 4, 0, 0, 0), 1, 1, RUNID);
        tdg.generateTraingData();
        SparkConf sparkConf = new SparkConf().setMaster("local[*]").setAppName("sparkTest");
        sparkCtx = new JavaSparkContext(sparkConf);
    }

    @Test
    public void testTraingDataFeatureCount() throws Exception {
        String dataPath = "target/tmp/" + RUNID + "/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(sparkCtx.sc(), dataPath).toJavaRDD();
        //allData.take(4).forEach(l -> System.out.println(l));
        LabeledPoint lb = allData.take(1).get(0);
        System.out.println(lb);
        Assert.assertEquals(lb.features().toArray().length, 5);
    }

    @Test
    public void testTraingDataFeatureHasWeather() throws Exception {
        String dataPath = "target/tmp/" + RUNID + "/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(sparkCtx.sc(), dataPath).toJavaRDD();
        JavaRDD<LabeledPoint> filterData = allData.filter(l -> l.features().toArray()[4] == Double.valueOf(0));
        //allData.take(4).forEach(l -> System.out.println(l));
        LabeledPoint lb = filterData.take(1).get(0);
        System.out.println(lb);
        Assert.assertEquals(lb.features().toArray()[4], 0.0, 0);
    }

    @Test
    public void testTraingDataFeatureHasTemperature() throws Exception {
        String dataPath = "target/tmp/" + RUNID + "/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(sparkCtx.sc(), dataPath).toJavaRDD();
        JavaRDD<LabeledPoint> filterData = allData.filter(l -> l.features().toArray()[4] == Double.valueOf(SensorType.TEMPSENSOR.getSensorId()));
        //allData.take(4).forEach(l -> System.out.println(l));
        LabeledPoint lb = filterData.take(1).get(0);
        System.out.println(lb);
        Assert.assertEquals(lb.features().toArray()[4], Double.valueOf(SensorType.TEMPSENSOR.getSensorId()), 0);
    }

    @Test
    public void testTraingDataFeatureHasHumidty() throws Exception {
        String dataPath = "target/tmp/" + RUNID + "/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(sparkCtx.sc(), dataPath).toJavaRDD();
        JavaRDD<LabeledPoint> filterData = allData.filter(l -> l.features().toArray()[4] == Double.valueOf(SensorType.HUMIDSENSOR.getSensorId()));
        //allData.take(4).forEach(l -> System.out.println(l));
        LabeledPoint lb = filterData.take(1).get(0);
        System.out.println(lb);
        Assert.assertEquals(lb.features().toArray()[4], Double.valueOf(SensorType.HUMIDSENSOR.getSensorId()), 0);
    }

    @Test
    public void testTraingDataFeatureHasPressure() throws Exception {
        String dataPath = "target/tmp/" + RUNID + "/training.dat";

        JavaRDD<LabeledPoint> allData = MLUtils.loadLibSVMFile(sparkCtx.sc(), dataPath).toJavaRDD();
        JavaRDD<LabeledPoint> filterData = allData.filter(l -> l.features().toArray()[4] == Double.valueOf(SensorType.PRESSURESENSOR.getSensorId()));
        //allData.take(4).forEach(l -> System.out.println(l));
        LabeledPoint lb = filterData.take(1).get(0);
        System.out.println(lb);
        Assert.assertEquals(lb.features().toArray()[4], Double.valueOf(SensorType.PRESSURESENSOR.getSensorId()), 0);
    }


    @After
    public void finishTest() {
        if (sparkCtx != null && sparkCtx.isLocal()) sparkCtx.stop();
    }
}
