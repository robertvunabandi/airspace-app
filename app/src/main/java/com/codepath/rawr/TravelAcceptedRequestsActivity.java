package com.codepath.rawr;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.adapters.TravelAcceptedRequestsAdapter;
import com.codepath.rawr.models.ShippingRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TravelAcceptedRequestsActivity extends AppCompatActivity {

    public static String TAG = "TvlAcceptedRequestsAct";
    AsyncHttpClient client;

    RelativeLayout parentLayout;

    public String travelNoticeId;

    public TextView request_count;

    // Declaring variables for list of pending requests
    TravelAcceptedRequestsAdapter travelAcceptedRequestsAdapter;
    ArrayList<ShippingRequest> mAcceptedRequests;
    RecyclerView rv_accepted_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_accepted_requests);

        client = new AsyncHttpClient();

        parentLayout = (RelativeLayout) findViewById(R.id.rlPending);

        // getting travel notice info to call the database
        travelNoticeId = getIntent().getStringExtra("travel_notice_id");

        mAcceptedRequests = new ArrayList<>();
        travelAcceptedRequestsAdapter = new TravelAcceptedRequestsAdapter(mAcceptedRequests);
        rv_accepted_requests = (RecyclerView) findViewById(R.id.rv_accepted_requests);
        rv_accepted_requests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv_accepted_requests.setAdapter(travelAcceptedRequestsAdapter);

        request_count = (TextView) findViewById(R.id.tv_accepted_count);

        populateList(travelNoticeId);

    }

    private void populateList(String travelNoticeId_) {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", travelNoticeId_);

        client.get(RawrApp.DB_URL + "/request/get_from_travel_notice", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, String.format("%s", response));
                try {
                    JSONObject travelNotice = response.getJSONObject("travel_notice");
                    JSONObject userJson = response.getJSONObject("user");
                    JSONArray requests = response.getJSONArray("request");
                    processResponse(userJson, travelNotice, requests);
                    Log.e(TAG, String.format("%s", response));
                } catch (JSONException e) {
                    Log.e(TAG, String.format("JSON error in parsing JSON in travel notice get: %s", e));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR(1): %s", statusCode, errorResponse));
                //Toast.makeText(getBaseContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
                snackbarCallLong("You have not yet accepted any shipping requests");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("CODE: %s ERROR(3): %s", statusCode, responseString));
                Log.e(TAG, String.format("error 3"));
            }
        });
    }

    private void processResponse(JSONObject userJson, JSONObject travelNotice, JSONArray requests) {
        // add requests to recylcer view
        for (int i = 0; i < requests.length(); i++) {
            ShippingRequest shippingRequest = null;
            try {
                    shippingRequest = ShippingRequest.fromJSONServer(requests.getJSONObject(i), travelNotice, userJson);
                if (shippingRequest.isAccepted()) {
                    mAcceptedRequests.add(shippingRequest);
                    travelAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRequests.size() - 1);
                    request_count.setText("You have accepted " + mAcceptedRequests.size() + " requests");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void snackbarCall(String message, int length){
        Snackbar.make(parentLayout, String.format("%s", message), length).show();
    }
    public void snackbarCallIndefinite(String message){
        snackbarCall(message, Snackbar.LENGTH_INDEFINITE);
    }
    public void snackbarCallLong(String message){
        snackbarCall(message, Snackbar.LENGTH_LONG);
    }
    public void snackbarCallShort(String message){
        snackbarCall(message, Snackbar.LENGTH_SHORT);
    }
}
