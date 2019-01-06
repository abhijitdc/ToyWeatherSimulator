package com.toy.weather.util;

import com.toy.weather.component.GeoLocation;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class LocationSampleGeneratorTest {

    @Test
    public void testSampleNo() throws IOException {
        LocationSampleGenerator lcg = new LocationSampleGenerator();
        Assert.assertEquals(lcg.samples(10).size(), 10);
    }

    @Test
    public void testSampleLocation() throws IOException {
        LocationSampleGenerator lcg = new LocationSampleGenerator();
        Assert.assertTrue(lcg.samples(10).get(0) instanceof GeoLocation);
    }
}
