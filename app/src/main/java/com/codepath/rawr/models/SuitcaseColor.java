package com.codepath.rawr.models;

import com.codepath.rawr.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robertvunabandi on 7/21/17.
 */

public class SuitcaseColor {
    private static String[] colorStrings = new String[] {"Black", "White", "Red", "Purple", "Blue", "Green", "Yellow", "Orange", "Grey", "Rainbow"};

    public int colorInteger;

    public boolean isRainbow() {
        return this.colorInteger == colorStrings.length - 1;
    }

    public SuitcaseColor(int colorInt){
        // defaults to black
        if (colorInt >= colorStrings.length || colorInt < 0) {
            this.colorInteger = 0;
        } else {
            this.colorInteger = colorInt;
        }
    }

    public static int getStringLength() {
        return colorStrings.length;
    }

    public String getColorString(){
        return colorStrings[this.colorInteger];
    }

    public int getDrawableId(){
        Map<String, Integer> colorMap = new HashMap<String, Integer>();
        colorMap.put(colorStrings[0], R.color.suitcaseColorBlack);
        colorMap.put(colorStrings[1], R.color.suitcaseColorWhite);
        colorMap.put(colorStrings[2], R.color.suitcaseColorRed);
        colorMap.put(colorStrings[3], R.color.suitcaseColorPurple);
        colorMap.put(colorStrings[4], R.color.suitcaseColorBlue);
        colorMap.put(colorStrings[5], R.color.suitcaseColorGreen);
        colorMap.put(colorStrings[6], R.color.suitcaseColorYellow);
        colorMap.put(colorStrings[7], R.color.suitcaseColorOrange);
        colorMap.put(colorStrings[8], R.color.suitcaseColorGrey);
        colorMap.put(colorStrings[9], R.color.suitcaseColorBlack);

        if (this.colorInteger == colorStrings.length - 1) {
            // rri stands for random rainbow integer
            int rri = (int) Math.round(3*Math.random());
            switch (rri) {
                case 0: {
                    return R.drawable.im_rainbow_linear;
                }
                case 1: {
                    return R.drawable.im_rainbow_linear_tilted;
                }
                case 2: {
                    return R.drawable.im_rainbow_radial;
                }
                case 3: {
                    return R.drawable.im_rainbow_radial_fancy;
                }
                default: {
                    // this is unreachable anyway!
                    return getDrawableId(); // makes a recursive call until it reaches a value
                }
            }
        } else {
            return colorMap.get(getColorString());
        }
    }

    public int getColorInteger(){
        return  this.colorInteger;
    }
}
