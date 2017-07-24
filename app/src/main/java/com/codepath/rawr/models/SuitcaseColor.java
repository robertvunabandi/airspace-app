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

    public SuitcaseColor(int colorInt){
        // defaults to black
        if (colorInt >= colorStrings.length || colorInt < 0) {
            this.colorInteger = 0;
        } else {
            this.colorInteger = colorInt;
        }
    }

    public String getColorString(){
        return colorStrings[this.colorInteger];
    }

    public int getColorId(){
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
            // TODO - Return rainbow gradient and return that instead of what's there
            return colorMap.get(getColorString());
        } else {
            return colorMap.get(getColorString());
        }
    }

    public int getColorInteger(){
        return  this.colorInteger;
    }
}
