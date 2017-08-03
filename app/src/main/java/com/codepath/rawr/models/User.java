package com.codepath.rawr.models;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/21/17.
 */

public class User {
    public String id, fName, lName, email, location, favoriteTravelPlace, phone;
    public SuitcaseColor suitcaseColor;
    public int dobDay, dobMonth, dobYear;
    public JSONArray travelNoticesIds, requestsIds, chatCollections;
    // extra stuff recently added
    public int tripsTaken, itemsSent;
    public double dollarsMade;
    public RawrDate dateCreated;

    /* {"Black", "White", "Red", "Purple", "Blue", "Green", "Yellow", "Orange", "Grey"} , int is the index of this array */

    public static String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public String getDOBSimple(){
        // returns the date of birth date in simple format, e.g.: 08/12/17
        String month = this.dobMonth < 10 ? "0" + Integer.toString(this.dobMonth) : Integer.toString(this.dobMonth);
        String day = this.dobDay < 10 ? "0" + Integer.toString(this.dobDay) : Integer.toString(this.dobDay);
        String year = Integer.toString(this.dobYear).substring(2);  // gives just 17 instead of 2017 for example
        return month + "/" + day + "/" + year;
    }

    public String getDOBVerbose(){
        // returns the date of birth in a verbose format, e.g.: August 12, 2017
        return months[this.dobMonth - 1] + " " + Integer.toString(this.dobDay) + ", " + Integer.toString(this.dobYear);
    }

    public String getFullName(){
        // returns the full name of this user object
        return this.fName + " " + this.lName;
    }

    public String getFullNameFormal(){
        // returns the full name of this user object
        return this.lName + ", " + this.fName;
    }

    public static User fromJSONServer(JSONObject response) throws JSONException {
        User user = new User();

        // populate this new user
        user.id = response.getString("_id");
        user.fName = response.getString("f_name");
        user.lName = response.getString("l_name");
        user.email = response.getString("email");
        // many of the following variables may be empty! so, we do a try catch so that no error gets thrown
        try {
            String date = response.getString("dob"); // string of the format MM/DD/YYYY
            user.dobDay = Integer.parseInt(date.substring(3,5));
            user.dobMonth = Integer.parseInt(date.substring(0,2));
            user.dobYear = Integer.parseInt(date.substring(6,10));
        } catch (Exception e) {
            user.dobDay = Integer.MIN_VALUE;
            user.dobMonth = Integer.MIN_VALUE;
            user.dobYear = Integer.MIN_VALUE;
        }

        user.location = response.getString("location");
        user.favoriteTravelPlace = response.getString("favorite_travel_place");
        user.suitcaseColor = new SuitcaseColor(response.getInt("suitcase_color_integer")); // see SuitcaseColor model!

        try {
            user.phone = response.getString("phone");
        } catch (Exception e) {
            user.phone = "";
        }
        try {
            user.travelNoticesIds = response.getJSONArray("travel_notices_ids");
        } catch (Exception e) {
            user.travelNoticesIds = null;
        }
        try {
            user.requestsIds = response.getJSONArray("requests_ids");
        } catch (Exception e) {
            user.requestsIds = null;
        }
        try {
            user.chatCollections = response.getJSONArray("chat_collections");
        } catch (Exception e) {
            user.chatCollections = null;
        }

        user.tripsTaken = response.getInt("trips_taken");
        user.itemsSent = response.getInt("items_sent");
        user.dollarsMade = response.getDouble("dollars_made");
        user.dateCreated = RawrDate.fromJSONServer(response.getJSONObject("date_created"));

        return user;
    }

    public RequestParams getParamsForProfileUrl() {
        RequestParams params = new RequestParams();
        params.put("user_id", this.id);
        return params;
    }
}
