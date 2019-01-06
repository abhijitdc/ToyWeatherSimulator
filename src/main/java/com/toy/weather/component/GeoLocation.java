package com.toy.weather.component;

import com.toy.weather.driver.MarkovProbVector;

/**
 * Created by abhijitdc on 1/4/19.
 * <p>
 * This class encapsulates a Geo location.It has helper methods to convert the location to
 * a specific zone and elevation category based on the latitude and elevation data. Both longitude and
 * elevation has been divided into three zones. These zones are used to select a probability vector for
 * Markov chain.
 * Any geo location would belong to either of these zones
 * Z1E1,Z1E2,Z1E3
 * Z2E1,Z2E2,Z2E3
 * Z3E1,Z3E2,Z3E3
 */
public class GeoLocation {

    public GeoLocation(double lati, double longi, int elv) {
        this.longi = longi;
        this.lati = lati;
        this.elv = elv;
    }

    private double longi, lati;
    private int elv;

    public double getLongi() {
        return longi;
    }

    public double getLati() {
        return lati;
    }

    public int getElv() {
        return elv;
    }

    /**
     * Gives this location's probability Vector for weather condition transition.
     *
     * @return MarkovProbVector
     */
    public MarkovProbVector getMarkovProbVector() {
        int zNo = 0, eNo = 0;

        if (Math.abs(lati) / 30 >= 2)
            zNo = 3;
        else if (Math.abs(lati) / 30 > 1)
            zNo = 2;
        else if (Math.abs(lati) / 30 <= 1)
            zNo = 1;

        if (Math.abs(elv) / 77 >= 2)
            eNo = 3;
        else if (Math.abs(elv) / 77 > 1)
            eNo = 2;
        else if (Math.abs(elv) / 77 <= 1)
            eNo = 1;

        return MarkovProbVector.LOOKUP.get(zNo, eNo);

    }


    @Override
    public String toString() {
        return "GeoLocation{" +
                "longi=" + longi +
                ", lati=" + lati +
                ", elv=" + elv +
                '}';
    }
}
