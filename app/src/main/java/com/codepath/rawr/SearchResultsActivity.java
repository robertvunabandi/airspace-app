package com.codepath.rawr;

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


    // Database url
    public String[] DB_URLS;

    // Declaring client
    AsyncHttpClient client;

    RecyclerView rvSearchResults;
    SearchResultAdapter searchResultAdapter;
    ArrayList<TravelNotice> mSearchResults;
    String from;
    String to;
    int month_by;
    int day_by;
    int year_by;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        DB_URLS = new String[] {getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};

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

        getData();

    }


    private void populateList(JSONArray travelNoticeList) {
        for (int i = 0; i < travelNoticeList.length(); i++) {
            try {
                TravelNotice travelNotice = TravelNotice.fromJSONServer(travelNoticeList.getJSONObject(i));
                mSearchResults.add(travelNotice);
                searchResultAdapter.notifyItemInserted(mSearchResults.size() - 1);
                // Toast.makeText(this, String.format("%s", travelNotice), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e("E", String.format("Error occured in JSON parsing"));
                e.printStackTrace();
                Toast.makeText(this, String.format("%s", e), Toast.LENGTH_LONG).show();
            }
        }
    }





    private void getData() {

        // Temporary tuid
        String traveler_id = "596d0b5626bffc280b32187e";

        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("to", to);
        params.put("from", from);
        params.put("day_by", day_by);
        params.put("month_by", month_by);
        params.put("year_by", year_by);


        client.get(DB_URLS[0] + "/travels", params, new JsonHttpResponseHandler() {

            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                searchResultAdapter.clear();

                try {
                    populateList(response.getJSONArray("data"));
                    // Toast.makeText(getBaseContext(), String.format("%s", response), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse){
                Toast.makeText(getBaseContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, Throwable throwable, JSONArray
                    errorResponse){
                Toast.makeText(getBaseContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, String responseString, Throwable
                    throwable){
                Toast.makeText(getBaseContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
