package com.codepath.rawr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.codepath.rawr.adapters.SearchResultAdapter;
import com.codepath.rawr.models.TravelNotice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchResultsActivity extends AppCompatActivity {
    // for debugging
    public static final String TAG = "SearchResultsActivity";
    // Database url and server stuff
    public String[] DB_URLS;
    AsyncHttpClient client;
    String from;
    String to;
    int month_by;
    int day_by;
    int year_by;
    // recycler view stuffs
    RecyclerView rvSearchResults;
    SearchResultAdapter searchResultAdapter;
    ArrayList<TravelNotice> mSearchResults;
    // views
    RelativeLayout parentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // initialize DB variables
        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        client = new AsyncHttpClient();
        // get the views
         parentLayout = (RelativeLayout) findViewById(R.id.relativeLayoutSearchResultsActivity);
        // get extras from intent
        from = getIntent().getExtras().getString("from");
        to = getIntent().getExtras().getString("to");
        month_by = getIntent().getExtras().getInt("month");
        day_by = getIntent().getExtras().getInt("dayOfMonth");
        year_by = getIntent().getExtras().getInt("year");
        // find the RecyclerView
        mSearchResults = new ArrayList<>();
        searchResultAdapter = new SearchResultAdapter(mSearchResults);
        rvSearchResults = (RecyclerView) findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchResultAdapter);
        rvSearchResults.setNestedScrollingEnabled(false);
        // make the search call
        getData();
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

    // populates the recycler view
    private void populateList(JSONArray travelNoticeList) {
        for (int i = 0; i < travelNoticeList.length(); i++) {
            try {
                // gets the travel notice form the response in the server and populates it into the adapter
                TravelNotice travelNotice = TravelNotice.fromJSONServer(travelNoticeList.getJSONObject(i));
                mSearchResults.add(travelNotice);
                searchResultAdapter.notifyItemInserted(mSearchResults.size() - 1);
            } catch (JSONException e) {
                Log.e("E", String.format("Error occured in JSON parsing"));
                e.printStackTrace();
                Intent data = new Intent();
                data.putExtra("message", "Error while parsing JSON in populateList method");
                setResult(RESULT_CANCELED, data); finish();
            }
        }
    }

    private void getData() {
        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId()); // we need to identify the user that's using the app so that we don't return his travel notices
        params.put("to", to);
        params.put("from", from);
        params.put("day_by", day_by);
        params.put("month_by", month_by);
        params.put("year_by", year_by);
        // make a call to Server to return a list of travels that matches the search
        client.get(DB_URLS[0] + "/travels", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                searchResultAdapter.clear();
                try {
                    // on result, populate the recyclerview with this list of datas
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                    Log.e(TAG, String.format("CODE: %s, ERROR(JSON): %s", statusCode, e));
                    Intent data = new Intent();
                    data.putExtra("message", String.format("JSON Exception occurred in /travels call: %s", e));
                    setResult(RESULT_CANCELED, data); finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR(1): %s", statusCode, errorResponse));
                Intent data = new Intent();
                try {
                    data.putExtra("message", String.format("Error occurred (1): %s", errorResponse.getString("message")));
                    // make a snackbar call
                    snackbarCallIndefinite(String.format("%s", errorResponse.getString("message")));
                } catch (JSONException e) {
                    data.putExtra("message", String.format("Error occurred (1)"));
                    // make a snackbar call
                    snackbarCallIndefinite("No results were found.");
                }
                setResult(RESULT_CANCELED, data); //finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("CODE: %s ERROR(3): %s", statusCode, responseString));
                Intent data = new Intent();
                data.putExtra("message", String.format("Error occurred (3): %s", responseString));
                // make a snackbar call
                snackbarCallIndefinite(String.format("ERROR (3): %s. No results were found.", responseString));
                setResult(RESULT_CANCELED, data); //finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // success, pass onto it the intent data, which should contain a "message" saying something
            String s = data.getStringExtra("message");
            setResult(RESULT_OK, data); finish();
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // failure, pass onto it the intent data, which should contain a "message" saying something
            String s = data.getStringExtra("message");
            snackbarCallShort(s);
        }
    }

//    @Override
//    public void onBackPressed() {
//        Intent data = new Intent();
//        data.putExtra("message", String.format("Cancelled"));
//        setResult(RESULT_CANCELED, data); finish();
//    }
}
