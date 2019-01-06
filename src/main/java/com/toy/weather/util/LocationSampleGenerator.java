package com.toy.weather.util;

import com.toy.weather.component.GeoLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by abhijitdc on 1/4/19.
 * <p>
 * This is an utility program to read the world's topography data from bitmap elevation_DE.BMP. The bitmap file has elevation data in the RED color channel.
 * <p>
 * The bitmap has a pixel height of 540 and width of 1080, so it seems that there was an observation made
 * for elevation every 20 minutes of latitude and longitude change.
 * The left uppermost corner has the pixel x,y as 0,0, accordingly with 20 minutes shift all the
 * pixels were translated into real world earth coordinate with latitude and longitude.
 * 0,0 --------------
 * |
 * |
 * |
 * |
 * |
 */
public class LocationSampleGenerator {

    private int width = 1080, height = 540;

    /**
     * Randomly select geo locations from the bitmap.
     *
     * @param sampleNo - number of geo locations to be selected at random.
     * @return
     * @throws IOException
     */
    public List<GeoLocation> samples(int sampleNo) throws IOException {
        BufferedImage img = ImageIO.read(new File("src/main/resources/elevation_DE.BMP"));

        int red;
        List<GeoLocation> sampleLocations = new ArrayList<>();

        Random rd = new Random();
        for (int i = 0; i < sampleNo; i++) {
            int x = rd.nextInt(width);
            int y = rd.nextInt(height);
            int rgb = img.getRGB(x, y);
            red = (rgb >> 16) & 0x000000FF;

            sampleLocations.add(new GeoLocation(latitudeConverter(y), longitudeConverter(x), red));
        }

        return sampleLocations;
    }

    public void printPixel(int x, int y) throws IOException {
        BufferedImage img = ImageIO.read(new File("src/main/resources/elevation_DE.BMP"));
        int red;
        int rgb = img.getRGB(x, y);
        Color c = new Color(rgb);
        System.out.println(" ISR " + c.getRed());
        red = (rgb >> 16) & 0x000000FF;
        System.out.println("RGB " + rgb);
        System.out.println(String.format("%d %d %d", x, y, red));
        System.out.println(String.format("%.2f %.2f %d", latitudeConverter(y), longitudeConverter(x), red));

    }

    /**
     * Converts the Y coordinate of a pixel into real world longitude value.
     *
     * @param l - Y coordinate value
     * @return
     */
    public Double latitudeConverter(int l) {
        long mins = 0;
        if (l <= 269)
            mins = (269 - l) * 20;
        else
            mins = (l - 269) * 20;

        Double result = Double.parseDouble((mins / 60) + "." + (mins % 60));
        result = result * ((l < 270) ? 1 : -1);
        return result;
    }

    /**
     * Converts the X coordinate of a pixel into real world latitude value.
     *
     * @param l - X coordinate value
     * @return
     */
    public Double longitudeConverter(int l) {
        long mins = 0;
        if (l <= 539)
            mins = (539 - l) * 20;
        else
            mins = (l - 539) * 20;

        Double result = Double.parseDouble((mins / 60) + "." + (mins % 60));
        result = result * ((l < 540) ? -1 : 1);
        return result;
    }

    public static void main(String args[]) throws Exception {
        LocationSampleGenerator lcg = new LocationSampleGenerator();
        lcg.printPixel(804, 178);
    }

}
