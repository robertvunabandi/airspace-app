package com.codepath.rawr.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/28/17.
 */

public class RawrDate {
    public int day, month, year, hour, min, sec;
    public String dateSimple, dateVerbose;
    public String getTime12format() {
        String hour;
        if (this.hour < 13) {
            hour = this.hour < 10 ? "0" + String.valueOf(this.hour) : String.valueOf(this.hour);
        } else {
            int tempHour = this.hour - 12;
            hour = tempHour < 12 ? "0" + String.valueOf(tempHour) : String.valueOf(tempHour);
        }
        String min = this.min < 10 ? "0" + String.valueOf(this.min) : String.valueOf(this.min);
        String ampm = this.hour < 12 ? "am" : "pm";
        return hour + ":" + min + " " + ampm;
    }
    public String getTime24format() {
        String hour = this.hour < 10 ? "0" + String.valueOf(this.hour): String.valueOf(this.hour);
        String min = this.min < 10 ? "0" + String.valueOf(this.min): String.valueOf(this.min);
        return hour + ":" + min;
    }
    public static String simpleDateFromDDMMYYYY(int day, int month, int year) {
        // returns the arrival date in simple format, e.g.: 08/12/17
        String mm = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);
        String dd = day < 10 ? "0" + Integer.toString(day) : Integer.toString(day);
        String yyyy = Integer.toString(year).substring(2); // gives just 17 instead of 2017 for example
        return mm + "/" + dd + "/" + yyyy;
    }
    public static String verboseDateFromDDMMYYYY(int day, int month, int year) {
        return TravelNotice.months[month - 1] + " " + Integer.toString(day) + ", " + Integer.toString(year);
    }
    public static RawrDate fromJSONServer(JSONObject jsonObject) throws JSONException {
        RawrDate rnd = new RawrDate();
        rnd.day = jsonObject.getInt("day");
        rnd.month = jsonObject.getInt("month");
        rnd.year = jsonObject.getInt("year");
        rnd.hour = jsonObject.getInt("hour");
        rnd.min = jsonObject.getInt("min");
        rnd.sec = jsonObject.getInt("sec");
        rnd.dateSimple = jsonObject.getString("simple");
        rnd.dateVerbose = jsonObject.getString("verbose");
        return rnd;
    }
}
