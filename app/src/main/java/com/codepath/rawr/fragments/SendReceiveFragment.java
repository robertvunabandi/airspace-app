package com.codepath.rawr.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.SearchResultsActivity;
import com.codepath.rawr.adapters.ShippingAcceptedRequestsAdapter;
import com.codepath.rawr.adapters.ShippingPendingRequestsAdapter;
import com.codepath.rawr.models.ShippingRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class SendReceiveFragment extends Fragment {
    // for database, we need client and urls
    AsyncHttpClient client;
    public String[] DB_URLS;
    // for results
    private static final int CODE_SENDER_FORM_ACTIVITY = 1;

    // Declaring variables for Pending Requests
    ShippingPendingRequestsAdapter shippingPendingRequestsAdapter;
    ArrayList<ShippingRequest> mPendingRqs;
    RecyclerView rv_pendingRequests;

    SwipeRefreshLayout swipeContainer;

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
        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        getRequestsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_receive, container, false);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getRequestsData();
            }
        });

        // instantiate views in layout
        final EditText et_from = (EditText) v.findViewById(R.id.et_from);
        final EditText et_to = (EditText) v.findViewById(R.id.et_to);
        final EditText et_date = (EditText) v.findViewById(R.id.et_date);
        // text input layouts, not used but may be used...
        TextInputLayout dateWrapper = (TextInputLayout) v.findViewById(R.id.dateWrapper);
        final TextInputLayout til_from = (TextInputLayout) v.findViewById(R.id.til_from);
        TextInputLayout til_to = (TextInputLayout) v.findViewById(R.id.til_to);


        /*
        // Everything that follow in this comment is for the filters, which may be done as a stretch
        final ImageView ivToggleFilter = (ImageView) v.findViewById(R.id.iv_toggleFilters);
        final ExpandableRelativeLayout erlFilter = (ExpandableRelativeLayout) v.findViewById(R.id.erl_filters);
        ivToggleFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Toggle the expandable view
                erlFilter.toggle();
                // to do - Change the drawable to either expanded or collapsed
                // to do - Add filters in XML
            }
        }); */

        // submit button stuff
        Button btSearch = (Button) v.findViewById(R.id.bt_search);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchResultsActivity.class);
                /* inside of SearchResultsActivity we will call the database using the
                *from* *to* and *by* parameters typed in here, and there in SRA we will get
                back an array of TravelNotices to put into the recycler view */
                i.putExtra("from", et_from.getText().toString());
                i.putExtra("to", et_to.getText().toString());
                // get month day and year and send through intent
                int month = Integer.parseInt(et_date.getText().toString().substring(0, 2));
                int dayOfMonth = Integer.parseInt(et_date.getText().toString().substring(3, 5));
                int year = Integer.parseInt(et_date.getText().toString().substring(6, 8)) + 2000;
                i.putExtra("month", month);
                i.putExtra("dayOfMonth", dayOfMonth);
                i.putExtra("year", year);
                // start activity for result for displaying snackbars for UX
                getActivity().startActivityForResult(i, RawrApp.CODE_REQUESTER_FORMS_ACTIVITY);
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

    public void clearViews() {
        // cv stands for clear views
        EditText et_from = (EditText) getView().findViewById(R.id.et_from);
        EditText et_to = (EditText) getView().findViewById(R.id.et_to);
        EditText et_date = (EditText) getView().findViewById(R.id.et_date);
        et_from.setText("");
        et_to.setText("");
        et_date.setText("");
    }

    public void refreshRequests() {
        getRequestsData();
    }

    // get data for list of trips
    private void getRequestsData() {

        client = new AsyncHttpClient();

        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());

        client.get(DB_URLS[0] + "/request_get_my", params, new JsonHttpResponseHandler() {
            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                shippingPendingRequestsAdapter.clear();
                shippingAcceptedRequestsAdapter.clear();
                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "An error occurred while parsing the JSON Array", Toast.LENGTH_LONG).show();
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateList(final JSONArray requestsList) {
        // loops through each id the the array, then make a call to db to get the travel notice
        for (int i = 0; i < requestsList.length(); i++) {
            try {
                // getting the travel notice ID
                RequestParams params = new RequestParams();
                params.put("travel_notice_id", requestsList.getJSONObject(i).getString("travel_notice_id"));
                final int finalI = i;
                client.get(DB_URLS[0] + "/travel_notice_get", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            ShippingRequest shippingRequest = ShippingRequest.fromJSONServer(requestsList.getJSONObject(finalI), response.getJSONObject("data"));
                            // if it's accepted, add it to the accepted RV, otherwise add it to the pending RV
                            if (shippingRequest.isAccepted()) {
                                mAcceptedRqs.add(shippingRequest);
                                shippingAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRqs.size() - 1);
                            } else if (shippingRequest.isPending()){
                                mPendingRqs.add(shippingRequest);
                                shippingPendingRequestsAdapter.notifyItemInserted(mPendingRqs.size() - 1);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), String.format("JSON error in parsing JSON in travel notice get: %s", e), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, String.format("CODE: %s ERROR(1): %s", statusCode, errorResponse));
                        Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e(TAG, String.format("CODE: %s ERROR(2): %s", statusCode, errorResponse));
                        Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, String.format("CODE: %s ERROR(3): %s", statusCode, responseString));
                        Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("Error occurred in JSON parsing for the whole try catch"));
                Toast.makeText(getContext(), String.format("%s", e), Toast.LENGTH_LONG).show();
            }
        }
    }
}
