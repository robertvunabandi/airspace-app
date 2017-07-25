package com.codepath.rawr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
    // for results
    private static final int CODE_SENDER_FORM_ACTIVITY = 1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // initialize DB variables
        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        client = new AsyncHttpClient();
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
        // make the search call
        getData();
    }

    // populates the recycler view
    private void populateList(JSONArray travelNoticeList) {
        for (int i = 0; i < travelNoticeList.length(); i++) {
            try {
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
                    Log.e(TAG, String.format("CODE: %s ERROR(JSON): %s", statusCode, e));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR(1): %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR(2): %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("CODE: %s ERROR(3): %s", statusCode, responseString));
                Toast.makeText(getBaseContext(), String.format("error 3 %s", responseString), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CODE_SENDER_FORM_ACTIVITY) {
            // success, pass onto it the intent data, which should contain a "message" saying something
            setResult(RESULT_OK, data); finish();
        } else if (resultCode == RESULT_CANCELED && requestCode == CODE_SENDER_FORM_ACTIVITY) {
            // failure, pass onto it the intent data, which should contain a "message" saying something
            setResult(RESULT_CANCELED, data); finish();
        }
    }
}
