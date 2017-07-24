package com.codepath.rawr.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.rawr.R;
import com.codepath.rawr.SearchResultsActivity;
import com.codepath.rawr.adapters.ShippingAcceptedRequestsAdapter;
import com.codepath.rawr.adapters.ShippingPendingRequestsAdapter;
import com.codepath.rawr.models.ShippingRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class SendReceiveFragment extends Fragment {

    // Database url
    public final static String DB_HEROKU_URL = "http://mysterious-headland-54722.herokuapp.com";
    public final static String DB_LOCAL_URL = "http://172.22.8.106:3000";
    public final static String[] DB_URLS = {DB_HEROKU_URL, DB_LOCAL_URL};

    // base URL for API
    public final static String API_BASE_URL = "https://api.flightstats.com/flex/schedules/rest";
    // parameter name for API key
    public final static String APP_KEY_PARAM = "appKey";
    public final static String APP_ID_PARAM = "appId";

    // Declaring client
    AsyncHttpClient client;

    // Temporary tuid
    String traveler_id = "596d0b5626bffc280b32187e";






    // Declaring variables for Pending Requests
    ShippingPendingRequestsAdapter shippingPendingRequestsAdapter;
    ArrayList<ShippingRequest> mPendingRqs;
    RecyclerView rv_pendingRequests;

    // Declaring variables for Accepted Requests
    ShippingAcceptedRequestsAdapter shippingAcceptedRequestsAdapter;
    ArrayList<ShippingRequest> mAcceptedRqs;
    RecyclerView rv_acceptedRequests;







    public SendReceiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getRequestsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_receive, container, false);

        final EditText et_from = (EditText) v.findViewById(R.id.et_from);
        final EditText et_to = (EditText) v.findViewById(R.id.et_to);
        final EditText et_date = (EditText) v.findViewById(R.id.et_date);
//        TextInputLayout dateWrapper = (TextInputLayout) v.findViewById(R.id.dateWrapper);
//        final TextInputLayout til_from = (TextInputLayout) v.findViewById(R.id.til_from);
//        TextInputLayout til_to = (TextInputLayout) v.findViewById(R.id.til_to);
        Button btSearch = (Button) v.findViewById(R.id.bt_search);
        final ImageView ivToggleFilter = (ImageView) v.findViewById(R.id.iv_toggleFilters);
        final ExpandableRelativeLayout erlFilter = (ExpandableRelativeLayout) v.findViewById(R.id.erl_filters);


        ivToggleFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Toggle the expandable view
                erlFilter.toggle();

                // TODO - Change the drawable to either expanded or collapsed
                // TODO - Add filters in XML
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchResultsActivity.class);

                // inside of SearchResultsActivity we will call the database using the
                // *from* *to* and *by* parameters typed in here, and there in SRA we will get
                // back an array of TravelNotices to put into the recycler view

                i.putExtra("from", et_from.getText().toString());
                i.putExtra("to", et_to.getText().toString());

                int month = Integer.parseInt(et_date.getText().toString().substring(0, 2));
                int dayOfMonth = Integer.parseInt(et_date.getText().toString().substring(3, 5));
                int year = Integer.parseInt(et_date.getText().toString().substring(6, 8)) + 2000;

                i.putExtra("month", month);
                i.putExtra("dayOfMonth", dayOfMonth);
                i.putExtra("year", year);

                getContext().startActivity(i);
            }
        });

        // Calendar
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // updateLabel();
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                et_date.setText(sdf.format(myCalendar.getTime()));
            }
        };

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });





        // Stuff for Pending Requests
        mPendingRqs = new ArrayList<>();
        shippingPendingRequestsAdapter = new ShippingPendingRequestsAdapter(mPendingRqs);
        rv_pendingRequests = (RecyclerView) v.findViewById(R.id.rv_pending_requests);
        rv_pendingRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_pendingRequests.setAdapter(shippingPendingRequestsAdapter);





        // stuff for Accepted Requests
        mAcceptedRqs = new ArrayList<>();
        shippingAcceptedRequestsAdapter = new ShippingAcceptedRequestsAdapter(mAcceptedRqs);
        rv_acceptedRequests = (RecyclerView) v.findViewById(R.id.rv_accepted_requests);
        rv_acceptedRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_acceptedRequests.setAdapter(shippingAcceptedRequestsAdapter);










        return v;
    }











    // get data for list of trips
    private void getRequestsData() {
        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("uid", traveler_id);

        /*

        client.get(DB_URLS[0] + "/requests_get_my", params, new JsonHttpResponseHandler() {

            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                shippingPendingRequestsAdapter.clear();
                shippingAcceptedRequestsAdapter.clear();

                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse) {
                Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray
                    errorResponse) {
                Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                    throwable) {
                Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
            }
        });   */
    }


    private void populateList(JSONArray requestsList) {
        for (int i = 0; i < requestsList.length(); i++) {
            try {
                ShippingRequest shippingRequest = ShippingRequest.fromJSONServer(requestsList.getJSONObject(i));

                if (shippingRequest.accepted == true) {
                    mAcceptedRqs.add(shippingRequest);
                    shippingAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRqs.size() - 1);
                }
                else {
                    mPendingRqs.add(shippingRequest);
                    shippingPendingRequestsAdapter.notifyItemInserted(mPendingRqs.size() - 1);
                    shippingPendingRequestsAdapter.notifyItemInserted(mPendingRqs.size() - 1);
                }
                // Toast.makeText(getContext(), String.format("%s", travelNotice), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.e(TAG, String.format("Error occurred in JSON parsing"));
                e.printStackTrace();
                Toast.makeText(getContext(), String.format("%s", e), Toast.LENGTH_LONG).show();
            }
        }
    }
}
