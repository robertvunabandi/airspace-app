package com.codepath.rawr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.codepath.rawr.adapters.TravelAcceptedRequestsAdapter;
import com.codepath.rawr.models.ShippingRequest;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;

public class TravelAcceptedRequestsActivity extends AppCompatActivity {

    public static String TAG = "Accepted Req";
    AsyncHttpClient client;
    public String[] DB_URLS;
    public String travelNoticeId;

    // Declaring variables for list of pending requests
    TravelAcceptedRequestsAdapter travelAcceptedRequestsAdapter;
    ArrayList<ShippingRequest> mRequests;
    RecyclerView rv_pending_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_accepted_requests);
    }
}
