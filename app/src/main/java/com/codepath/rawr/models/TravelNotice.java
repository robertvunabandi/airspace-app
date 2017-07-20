package com.codepath.rawr.models;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/20/17.
 */

public class TravelNotice {
    // required: 3
    public String id, tuid, airline, airline_name, flight_num;
    // optional: 6
    public Boolean item_envelopes, item_smbox, item_lgbox, item_clothing, item_fragile, item_liquid, item_other;
    public String drop_off_flexibility, pick_up_flexibility;
    // required: 12
    public String dep_iata, dep_city, dep_airport_name;
    public int dep_min, dep_hour, dep_day, dep_month, dep_year;
    public String arr_iata, arr_city, arr_airport_name;
    public int arr_min, arr_hour, arr_day, arr_month, arr_year;
    public JSONArray requests_ids;

    // for verbose method of dates
    public String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public final static String TAG = "TravelNoticeModel";

    // empty method for parcel just in case
    public TravelNotice() {
    }

    public static TravelNotice fromJSON(JSONObject response, String tuid, Boolean[] itemBools, String[] flexibilities) throws JSONException {
        // initiate an empty travelNotice
        TravelNotice tvl = new TravelNotice();

        // create variables that will be to make code clearer
        JSONObject scheduledFlights = response.getJSONArray("scheduledFlights").getJSONObject(0);
        JSONArray airports = response.getJSONObject("appendix").getJSONArray("airports");
        JSONObject airline = response.getJSONObject("appendix").getJSONArray("airlines").getJSONObject(0);

        // get the important required informations into tvl
        tvl.id = null;
        tvl.tuid = tuid;
        tvl.airline = scheduledFlights.getString("carrierFsCode");
        tvl.airline_name = airline.getString("name");
        tvl.flight_num = scheduledFlights.getString("flightNumber");

        // add the informations for filtering, a lot of which passed onto in arguments
        // set everything to true (assume traveler is okay with carrying anything) and set item_other to false and item_other_name to null
        tvl.item_envelopes = true;
        tvl.item_smbox = true;
        tvl.item_lgbox = true;
        tvl.item_clothing = true;
        tvl.item_fragile = true;
        tvl.item_liquid = true;
        tvl.item_other = true;

        if (flexibilities != null) {
            // if the flexibilities are put, update flexibilities
            tvl.drop_off_flexibility = flexibilities[0];
            tvl.pick_up_flexibility = flexibilities[1];
        } else {
            // otherwise set them to null
            tvl.drop_off_flexibility = null;
            tvl.pick_up_flexibility = null;
        }

        // add the departure informations
        tvl.dep_iata = scheduledFlights.getString("departureAirportFsCode");

        for (int i = 0; i < airports.length(); i++) {
            // check if this airport's iata matches that of the depature and then get the name
            if (airports.getJSONObject(i).getString("fs").equals(tvl.dep_iata)) {
                tvl.dep_airport_name = airports.getJSONObject(i).getString("name");
                tvl.dep_city = airports.getJSONObject(i).getString("city"); // GET THE CITY
            }
        }
        tvl.dep_hour = Integer.parseInt(scheduledFlights.getString("departureTime").substring(11, 13));
        tvl.dep_min = Integer.parseInt(scheduledFlights.getString("departureTime").substring(14, 16));
        tvl.dep_day = Integer.parseInt(scheduledFlights.getString("departureTime").substring(8, 10));
        tvl.dep_month = Integer.parseInt(scheduledFlights.getString("departureTime").substring(5, 7));
        tvl.dep_year = Integer.parseInt(scheduledFlights.getString("departureTime").substring(0, 4));

        // add the arrival information
        tvl.arr_iata = scheduledFlights.getString("arrivalAirportFsCode");
        for (int i = 0; i < airports.length(); i++) {
            // check if this airport's iata matches that of the depature and then get the name
            if (airports.getJSONObject(i).getString("fs").equals(tvl.arr_iata)) {
                tvl.arr_airport_name = airports.getJSONObject(i).getString("name");
                tvl.arr_city = airports.getJSONObject(i).getString("city"); // GET THE CITY
            }
        }
        tvl.arr_hour = Integer.parseInt(scheduledFlights.getString("arrivalTime").substring(11, 13));
        tvl.arr_min = Integer.parseInt(scheduledFlights.getString("arrivalTime").substring(14, 16));
        tvl.arr_day = Integer.parseInt(scheduledFlights.getString("arrivalTime").substring(8, 10));
        tvl.arr_month = Integer.parseInt(scheduledFlights.getString("arrivalTime").substring(5, 7));
        tvl.arr_year = Integer.parseInt(scheduledFlights.getString("arrivalTime").substring(0, 4));

        // return the tvl
        return tvl;
    }

    /* Creates parameters for sending a request to our database directly
    from this travel notice using its own data, which is why the "this". */
    public RequestParams createParams() {
        /* creates parameter for endpoint call /travel_notice_add and /travel_notice_update */
        // TODO - be careful about _id being null
        RequestParams params = new RequestParams();
        // required, 4 parameters
        params.put("_id", this.id);
        params.put("tuid", this.tuid);
        params.put("airline", this.airline);
        params.put("airline_name", this.airline_name);
        params.put("flight_num", this.flight_num);
        // optional: 7 parameters
        params.put("item_envelopes", this.item_envelopes);
        params.put("item_smbox", this.item_smbox);
        params.put("item_lgbox", this.item_lgbox);
        params.put("item_clothing", this.item_clothing);
        params.put("item_fragile", this.item_fragile);
        params.put("item_liquid", this.item_liquid);
        params.put("item_other", this.item_other);
        params.put("drop_off_flexibility", this.drop_off_flexibility);
        params.put("pick_up_flexibility", this.pick_up_flexibility);
        // required: 8 departure informations
        params.put("dep_airport_name", this.dep_airport_name);
        params.put("dep_iata", this.dep_iata);
        params.put("dep_city", this.dep_city);
        params.put("dep_min", this.dep_min);
        params.put("dep_hour", this.dep_hour);
        params.put("dep_day", this.dep_day);
        params.put("dep_month", this.dep_month);
        params.put("dep_year", this.dep_year);
        // required: 8 arrival informations
        params.put("arr_airport_name", this.arr_airport_name);
        params.put("arr_iata", this.arr_iata);
        params.put("arr_city", this.arr_city);
        params.put("arr_min", this.arr_min);
        params.put("arr_hour", this.arr_hour);
        params.put("arr_day", this.arr_day);
        params.put("arr_month", this.arr_month);
        params.put("arr_year", this.arr_year);
        params.put("requests_ids", this.requests_ids);
        return params;
    }

    /* Returns a boolean whether the flight is overnight or not */
    public boolean isOvernight() {
        // Unless a flight took a legit 30 days, the departure day and arrival day will be different if the flight is overnight
        return this.arr_day != this.dep_day;
    }

    public String getDepartureDaySimple() {
        // returns the depature date in simple format, e.g.: 08/12/17
        String month = this.dep_month < 10 ? "0" + Integer.toString(this.dep_month) : Integer.toString(this.dep_month);
        String day = this.dep_day < 10 ? "0" + Integer.toString(this.dep_day) : Integer.toString(this.dep_day);
        String year = Integer.toString(this.dep_year).substring(2);  // gives just 17 instead of 2017 for example
        return month + "/" + day + "/" + year;
    }

    public String getArrivalDaySimple() {
        // returns the arrival date in simple format, e.g.: 08/12/17
        String month = this.arr_month < 10 ? "0" + Integer.toString(this.arr_month) : Integer.toString(this.arr_month);
        String day = this.arr_day < 10 ? "0" + Integer.toString(this.arr_day) : Integer.toString(this.arr_day);
        String year = Integer.toString(this.arr_year).substring(2); // gives just 17 instead of 2017 for example
        return month + "/" + day + "/" + year;
    }

    public String getDepartureDayVerbose() {
        // returns the departure date in a verbose format, e.g.: August 12, 2017
        return months[this.dep_month - 1] + " " + Integer.toString(this.dep_day) + ", " + Integer.toString(this.dep_year);
    }

    public String getArrivalDayVerbose() {
        // returns the arrival date in a verbose format, e.g.: August 12, 2017
        return months[this.arr_month - 1] + " " + Integer.toString(this.arr_day) + ", " + Integer.toString(this.arr_year);
    }

    public String getDepartureTime() {
        // return the time like 12:00 PM
        int hour = this.dep_hour > 12 ? this.dep_hour - 12 : this.dep_hour;
        String finalHour = hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour);
        String finalMin = this.dep_min < 10 ? "0" + Integer.toString(this.dep_min) : Integer.toString(this.dep_min);
        String ampm = this.dep_hour >= 12 ? "PM" : "AM";
        return finalHour + ":" + finalMin + " " + ampm;
    }

    public String getArrivalTime() {
        // return the time like 12:00 PM
        int hour = this.arr_hour > 12 ? this.arr_hour - 12 : this.arr_hour;
        String finalHour = hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour);
        String finalMin = this.arr_min < 10 ? "0" + Integer.toString(this.arr_min) : Integer.toString(this.arr_min);
        String ampm = this.arr_hour >= 12 ? "PM" : "AM";
        return finalHour + ":" + finalMin + " " + ampm;
    }

    public static TravelNotice fromJSONServer(JSONObject response) throws JSONException {
        // initiate an empty travelNotice
        TravelNotice tvl = new TravelNotice();

        // get the important required informations into tvl
        tvl.id = response.getString("_id");
        tvl.tuid = response.getString("tuid");
        tvl.airline = response.getString("airline");
        tvl.airline_name = response.getString("airline_name");
        tvl.flight_num = response.getString("flight_num");

        // add the informations for filtering, a lot of which passed onto in arguments

        tvl.item_envelopes = response.getBoolean("item_envelopes");
        tvl.item_smbox = response.getBoolean("item_smbox");
        tvl.item_lgbox = response.getBoolean("item_lgbox");
        tvl.item_clothing = response.getBoolean("item_clothing");
        tvl.item_fragile = response.getBoolean("item_fragile");
        tvl.item_liquid = response.getBoolean("item_liquid");
        tvl.item_other = response.getBoolean("item_other");

        // if the flexibilities are put, update flexibilities
        try {
            // we do a try catch since both these infor
            tvl.drop_off_flexibility = response.getString("drop_off_flexibility");
        } catch (JSONException e) {
            tvl.drop_off_flexibility = null;
        }
        try {
            tvl.pick_up_flexibility = response.getString("pick_up_flexibility");
        } catch (JSONException e) {
            tvl.pick_up_flexibility = null;
        }

        // add the departure informations
        tvl.dep_airport_name = response.getString("dep_airport_name");
        tvl.dep_iata = response.getString("dep_iata");
        tvl.dep_city = response.getString("dep_city");
        tvl.dep_hour = response.getInt("dep_hour");
        tvl.dep_min = response.getInt("dep_min");
        tvl.dep_day = response.getInt("dep_day");
        tvl.dep_month = response.getInt("dep_month");
        tvl.dep_year = response.getInt("dep_year");

        // add the arrival information
        tvl.arr_airport_name = response.getString("arr_airport_name");
        tvl.arr_iata = response.getString("arr_iata");
        tvl.arr_city = response.getString("arr_city");
        tvl.arr_hour = response.getInt("arr_hour");
        tvl.arr_min = response.getInt("arr_min");
        tvl.arr_day = response.getInt("arr_day");
        tvl.arr_month = response.getInt("arr_month");
        tvl.arr_year = response.getInt("arr_year");

        tvl.requests_ids = response.getJSONArray("requests_ids");

        // return the tvl
        return tvl;
    }
}
