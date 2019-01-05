package com.toy.weather.process;

import com.toy.weather.component.GeoLocation;
import com.toy.weather.util.LocationSampleGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class SampleGenerator {

    private List<GeoLocation> sampleLocations;
    private LocalDateTime observationStartDate;
    private int noOfLocations;
    private int noOfSamplesPerLocation;

    public SampleGenerator(LocalDateTime observationStartDate, int noOfLocations, int noOfSamplesPerLocation) throws InstantiationException {
        try {
            this.observationStartDate = observationStartDate;
            this.noOfLocations = noOfLocations;
            this.noOfSamplesPerLocation = noOfSamplesPerLocation;
            this.sampleLocations = new LocationSampleGenerator().samples(noOfLocations);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InstantiationException();
        }
    }

    public void generateSamples() {

    }
}
