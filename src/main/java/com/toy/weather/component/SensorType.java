package com.toy.weather.component;

/**
 * Created by abhijitdc on 1/5/19.
 */
public enum SensorType {
    TEMPSENSOR(1, "Temp"),
    HUMIDSENSOR(2, "Humidity"),
    PRESSURESENSOR(3, "Pressure");

    private int sensorId;
    private String sensorName;

    public int getSensorId() {
        return sensorId;
    }

    public String getSensorName() {
        return sensorName;
    }


    SensorType(int sensorId, String sensorName) {
        this.sensorId = sensorId;
        this.sensorName = sensorName;
    }
}
