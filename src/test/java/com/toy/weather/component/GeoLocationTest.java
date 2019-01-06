package com.toy.weather.component;

import com.toy.weather.driver.MarkovProbVector;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by abhijitdc on 1/5/19.
 */
public class GeoLocationTest {

    @Test
    public void testLatitude(){
        GeoLocation glc = new GeoLocation(-91.1,128.21,28);
        Assert.assertNull(glc.getMarkovProbVector());
    }

    @Test
    public void testElevation(){
        GeoLocation glc = new GeoLocation(-91.1,128.21,-10);
        Assert.assertNull(glc.getMarkovProbVector());
    }

    @Test
    public void testGeoLocationZ1E1(){
        GeoLocation glc = new GeoLocation(-29.9,128.21,10);
        Assert.assertEquals(glc.getMarkovProbVector(), MarkovProbVector.Z1E1);
    }

    @Test
    public void testGeoLocationZ2E1(){
        GeoLocation glc = new GeoLocation(-30.9,128.21,70);
        Assert.assertEquals(glc.getMarkovProbVector(), MarkovProbVector.Z2E1);
    }
}
