package com.toy.weather.component;

import com.toy.weather.driver.MarkovProbVector;

/**
 * Created by abhijitdc on 1/4/19.
 */
public class GeoLocation {

    public GeoLocation(double longi, double lati, int elv) {
        this.longi = longi;
        this.lati = lati;
        this.elv = elv;
    }

    private double longi, lati;
    private int elv;

    public MarkovProbVector getMarkovProbVector() {
        int zNo = 0, eNo = 0;

        if (Math.abs(longi) / 30 > 2)
            zNo = 3;
        else if (Math.abs(longi) / 30 > 1)
            zNo = 2;
        else if (Math.abs(longi) / 30 < 1)
            zNo = 1;

        if (Math.abs(longi) / 77 > 2)
            eNo = 3;
        else if (Math.abs(longi) / 77 > 1)
            eNo = 2;
        else if (Math.abs(longi) / 77 < 1)
            eNo = 1;

        return MarkovProbVector.LOOKUP.get(zNo, eNo);

    }
}
