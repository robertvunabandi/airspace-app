package com.codepath.rawr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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

    public static String TAG = "Accepted Req";
    AsyncHttpClient client;
    public String[] DB_URLS;
    public String travelNoticeId;

    // Declaring variables for list of pending requests
    TravelAcceptedRequestsAdapter travelAcceptedRequestsAdapter;
    ArrayList<ShippingRequest> mAcceptedRequests;
    RecyclerView rv_accepted_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_accepted_requests);

        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        client = new AsyncHttpClient();

        // getting travel notice info to call the database
        travelNoticeId = getIntent().getStringExtra("travel_notice_id");

        mAcceptedRequests = new ArrayList<>();
        travelAcceptedRequestsAdapter = new TravelAcceptedRequestsAdapter(mAcceptedRequests);
        rv_accepted_requests = (RecyclerView) findViewById(R.id.rv_accepted_requests);
        rv_accepted_requests.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv_accepted_requests.setAdapter(travelAcceptedRequestsAdapter);

        populateList(travelNoticeId);
    }

    private void populateList(String travelNoticeId_) {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", travelNoticeId_);

        client.get(DB_URLS[0] + "/request/get_from_travel_notice", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, String.format("%s", response));
                try {
                    JSONObject travelNotice = response.getJSONObject("travel_notice");
                    JSONArray requests = response.getJSONArray("request");
                    processResponse(travelNotice, requests);
                    Toast.makeText(getBaseContext(), String.format("%s", response), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), String.format("JSON error in parsing JSON in travel notice get: %s", e), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        Log.e(TAG, String.format("CODE: %s ERROR(1): %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                        Log.e(TAG, String.format("CODE: %s ERROR(2): %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        Log.e(TAG, String.format("CODE: %s ERROR(3): %s", statusCode, responseString));
                Toast.makeText(getBaseContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processResponse(JSONObject travelNotice, JSONArray requests) {
        // add requests to recylcer view
        for (int i = 0; i < requests.length(); i++) {
            ShippingRequest shippingRequest = null;
            try {
                    shippingRequest = ShippingRequest.fromJSONServer(requests.getJSONObject(i), travelNotice);
                if (shippingRequest.isAccepted()) {
                    mAcceptedRequests.add(shippingRequest);
                    travelAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRequests.size() - 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
