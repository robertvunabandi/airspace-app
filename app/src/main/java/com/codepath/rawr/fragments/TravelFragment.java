package com.codepath.rawr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.rawr.R;
import com.codepath.rawr.adapters.UpcomingTripAdapter;
import com.codepath.rawr.models.TravelNotice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class TravelFragment extends Fragment {

    // Database url
    public final static String DB_HEROKU_URL = "http://mysterious-headland-54722.herokuapp.com";
    public final static String DB_LOCAL_URL = "http://172.22.8.106:3000";
    public final static String[] DB_URLS = {DB_HEROKU_URL, DB_LOCAL_URL};

    // Declaring client
    AsyncHttpClient client;

    UpcomingTripAdapter upcomingTripAdapter;
    ArrayList<TravelNotice> mTrips;
    RecyclerView rv_trips;

    public TravelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new AsyncHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        // find the RecyclerView
        mTrips = new ArrayList<>();
        upcomingTripAdapter = new UpcomingTripAdapter(mTrips);
        rv_trips = (RecyclerView) v.findViewById(R.id.rv_trips);
        rv_trips.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_trips.setAdapter(upcomingTripAdapter);

        return v;
    }

    private void populateList(JSONArray travelNoticeList) {
        for (int i = 0; i < travelNoticeList.length(); i++) {
            try {
                TravelNotice travelNotice = TravelNotice.fromJSONServer(travelNoticeList.getJSONObject(i));
                mTrips.add(travelNotice);
                upcomingTripAdapter.notifyItemInserted(mTrips.size() - 1);
//                Toast.makeText(getContext(), String.format("%s", travelNotice), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.e(TAG, String.format("Error occured in JSON parsing"));
                e.printStackTrace();
                Toast.makeText(getContext(), String.format("%s", e), Toast.LENGTH_LONG).show();
            }
        }

    private void getData() {

        // Temporary tuid
        String traveler_id = "596d0b5626bffc280b32187e";

        // Set the request parameters
        RequestParams params = new RequestParams();

        client.get(DB_URLS[0] + "/travel_notice_all", params, new JsonHttpResponseHandler() {

            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                upcomingTripAdapter.clear();

                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                }
            }
    }
}
