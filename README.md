# Toy Weather Simulator

## Objective

Create a toy simulation of the environment (taking into account things like atmosphere, topography, geography, oceanography, or similar) that evolves over time. Then take measurements at various locations and times, and have your program emit that data, as in the following:

```
 Location|latitude, longitude, elevation|ISO8601 date time|Conditions|Temperature|Pressure|humidity
 Sydney|-33.86,151.21,39|2015-12-23T05:02:12Z|Rain|+12.5|1004.3|97
 Melbourne|-37.83,144.98,7|2015-12-24T15:30:55Z|Snow|-5.3|998.4|55
 Adelaide|-34.92,138.62,48|2016-01-03T12:35:37Z|Sunny|+39.4|1114.1|12
```

##  Technology stack
       - Java 1.8
       - Maven (Build tool)
       - Apache Spark MLlib
       - Markov Process and probability distribution for training data generation
       - Predictive Model for simulated weather data generation
       - Random Forests (Classification and Regression)
       - LIBSVM data format for training data

## Training Data Generation

To generate the training data we are using a stochastic model ``(Markov Process)`` to first simulate weather condition change and then based on the weather condition we will generate other sensor measurements (temperature, pressure and humidity) from a plausible range.

For state transition in ``Markov chain`` we will utilize multiple ``Transition Matrix`` by classifying a Geo location into zones based on latitude and elevation. The absolute latitude and elevation value will be divided into three ranges which will corresponds to nine probability vectors.

**Transition Matrix** -
Following are the nine set of 3X3 *(sunny,rain,snow)* matrix, out of which one will be chosen based on latitude and elevation.

|Vector|Latitude Zone|Elevation|
|------|-------------|---------|
|Z1E1|lower|lower|
|Z1E2|lower|medium|
|Z1E3|lower|higher|
|Z2E1|medium|lower|
|Z2E2|medium|medium|
|Z2E3|medium|higher|
|Z3E1|higher|lower|
|Z3E2|higher|medium|
|Z3E3|higher|higher|

>Yah! earth has no tilt on it's axis :)

Multiple Geo locations will be chosen at random from the bitmap ``elevation_DE.BMP`` for generating the training data. This bitmap has real earth elevation data in the Red channel.

###### elevation_DE.BMP
 - Bitmap elevation_DE.BMP has a pixel height and width of 540 x 1080 and the RED color channel has the elevation data. Based on the size of the image we can assume that it has one elevation observation every 20 minutes of latitude or longitude change.
 - The top left corner has the (x,y) coordinate as (0,0), so accordingly the pixel coordinate will be used to translate into real world latitude and longitude.
 - The file is included in src/main/resources

## Predictive Modeling

**Note: This step is optional if only need to generate weather data for simulation (and not predict),  see details in "How to run it"**

We will train both Regression and Classification model for generating weather data based on latitude, longitude, elevation and time.

Regression Model will be utilized to predict measurement for sensors *(Temperature/Pressure/Humidity)* and classification model to predicting the *Weather Condition*.

We will utilize **Random Forest** to train both the regression and classification models.

>- Random forest algorithm can be used for both classifications and regression task.
>- It provides higher accuracy.
>- Random forest classifier will handle the missing values and maintain the accuracy of a large proportion of data.
If there are more trees, it won’t allow overfitting trees in the model.
>- It has the power to handle a large data set with higher dimensionality

Independent variable ~ Dependent variables

>- Temperature ~  Latitude + Longitude + Elevation + Day of the year
>- Humidity ~  Latitude + Longitude + Elevation + Day of the year
>- Pressure ~  Latitude + Longitude + Elevation + Day of the year
>- Weather Condition ~ Latitude + Longitude + Elevation + Day of the year

## How to run it

#### Build
Maven is the build tool used for this repo. It will build a fat-jar with all dependencies bundled inside, so it can be executed without any classpath setup for dependencies.

`mvn clean install`

Running mvn clean install will produce a fat jar target/app.jar

>Note: Running jUnit test during the build is disabled by default, please change pom.xml skipTests to false

#### Execute

This simulator has two run mode.

##### Run Mode 1 - With Predictive Model
- Generate training data using Markov Process (in LIBSVM format) and then utilize that data to train predictive models to predict weather data for other geo locations.

From the checkout directory run following command
`java -cp target/app.jar com.toy.weather.App <optional: path of input json>`

>- Note: Spark master is hardcoded as local so the jar will only execute locally and not get submitted to yarn.
Todo: allow option to run local vs. cluster

##### Run Mode 2 - No Predictive Model

- In the second mode, it will just use the Markov Process and produce the weather data for random set of locations in desired format.

From the checkout directory run following command
`java -cp target/app.jar com.toy.weather.Simulator <optional: path of input json>`

#### Input
The launch programs accepts a json file as an input. If no input is provided it will use a bundled input file from src/main/resources/input.json.

Input json Format
```
{
  "trainingLocationNum": 100,
  "trainingNumOfDays": 365,
  "trainingStartDate":"2010-03-02 11:22:10",
  "reportingLocationNum": 10,
  "reportingPerLocation": 100,
  "reportingStartDate":"2016-03-02 11:22:10"
}
```
>- trainingLocationNum - Number of Geo Location which will be chosen at random for training data generation.(Not required for Run Mode 2)
>- trainingNumOfDays - Number of consecutive days for which the data will be generated. One data point for each sensor per location.(Not required for Run Mode 2)
>- trainingStartDate - start date from which the training data will be generated.(Not required for Run Mode 2)
>- reportingLocationNum - Number of randomly chosen geo locations for which the model will predict weather data.
>- reportingPerLocation - Number of weather data set to be predicted for each location.
>- reportingStartDate - Start date from which the prediction needs to start.

### Output
Each run of the program will generate a RUNID from current system time and that values will be used to create a directory as ``target/RUNID`` to keep outputs separated from each run. This directory will have
- Training data *(RUNID/training.dat)*
- All the generated models
    - temperature *(RUNID/TempRegressionModel)*
    - pressure *(RUNID/PressureRegressionModel)*
    - humidity *(RUNID/HumidityRegressionModel)*
    - weather condition *(RUNID/weatherConditionClassifierModel)*
- final output data for Run Mode 1 *(RUNID/sampledata.dat)*
- final output data for Run Mode 2 *(RUNID/simulator.dat)*

## Reference

- https://www.dartmouth.edu/~chance/teaching_aids/books_articles/probability_book/Chapter11.pdf
- Other github users work on similar topic.
