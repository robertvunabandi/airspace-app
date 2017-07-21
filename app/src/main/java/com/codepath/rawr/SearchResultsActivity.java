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
    public final static String DB_HEROKU_URL = "http://mysterious-headland-54722.herokuapp.com";
    public final static String DB_LOCAL_URL = "http://172.22.8.106:3000";
    public final static String[] DB_URLS = {DB_HEROKU_URL, DB_LOCAL_URL};

    // Declaring client
    AsyncHttpClient client;

    RecyclerView rvSearchResults;
    SearchResultAdapter searchResultAdapter;
    ArrayList<TravelNotice> mSearchResults;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        client = new AsyncHttpClient();

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
                Toast.makeText(this, String.format("%s", travelNotice), Toast.LENGTH_LONG).show();
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

        client.get(DB_URLS[0] + "/travel_notice_all", params, new JsonHttpResponseHandler() {

            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                searchResultAdapter.clear();

                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse){
                // Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, Throwable throwable, JSONArray
                    errorResponse){
                // Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure ( int statusCode, Header[] headers, String responseString, Throwable
                    throwable){
                // Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
