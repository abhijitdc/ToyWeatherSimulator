package com.toy.weather.util;

import com.toy.weather.component.GeoLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * Created by abhijitdc on 1/4/19.
 */
public class LocationSampleGenerator {

    private int width = 1080, height = 540;

    public List<GeoLocation> samples(int sampleNo) throws IOException {
        BufferedImage img = ImageIO.read(new File("src/main/resources/elevation_DE.BMP"));

        int red;
        int green;
        int blue;
        Random rd = new Random();
        for (int i = 0; i < sampleNo; i++) {
            int x = rd.nextInt(width);
            int y = rd.nextInt(height);
            int rgb = img.getRGB(x, y);
            red = (rgb >> 16) & 0x000000FF;

            System.out.println(String.format("%d %d %d", x, y, red));
            System.out.println(String.format("%.2f %.2f %d", longitudeConverter(y), latitudeConverter(x), red));
        }

        return null;
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
        System.out.println(String.format("%.2f %.2f %d", longitudeConverter(y), latitudeConverter(x), red));

    }

    public Double longitudeConverter(int l) {
        long mins = 0;
        if (l <= 269)
            mins = (269 - l) * 20;
        else
            mins = (l - 269) * 20;

        Double result = Double.parseDouble((mins / 60) + "." + (mins % 60));
        result = result * ((l < 270) ? 1 : -1);
        return result;
    }

    public Double latitudeConverter(int l) {
        long mins = 0;
        if (l <= 539)
            mins = (539 - l) * 20;
        else
            mins = (l - 539) * 20;

        Double result = Double.parseDouble((mins / 60) + "." + (mins % 60));
        result = result * ((l < 540) ? -1 : 1);
        return result;
    }


    public static void main(String args[]) throws IOException {
        LocationSampleGenerator lSampler = new LocationSampleGenerator();
        lSampler.printPixel(770, 218);
    }

}
