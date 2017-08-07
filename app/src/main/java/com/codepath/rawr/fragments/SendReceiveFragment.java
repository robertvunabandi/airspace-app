package com.codepath.rawr.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.rawr.MainActivity;
import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.SearchResultsActivity;
import com.codepath.rawr.adapters.ShippingAcceptedRequestsAdapter;
import com.codepath.rawr.adapters.ShippingPendingRequestsAdapter;
import com.codepath.rawr.models.ShippingRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
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
    // TODO - Remove all the toast, and if necessary, replace with snackbars
    // TODO - FOR SNAKCBARS DO THIS: ((MainActivity) getActivity()).snackbarCallLong(<message>); // message is a STRING
    // for database, we need client and urls
    AsyncHttpClient client;
    // for results
    private static final int CODE_SENDER_FORM_ACTIVITY = 1;

    // Declaring variables for Pending Requests
    ShippingPendingRequestsAdapter shippingPendingRequestsAdapter;
    ArrayList<ShippingRequest> mPendingRqs;
    RecyclerView rv_pendingRequests;

    SwipeRefreshLayout swipeContainer;
    TextView tv_pending_counter;
    TextView tv_accepted_counter;

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
        client = new AsyncHttpClient();
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

        // setting up the expandable layout for adding a trip
        final ExpandableRelativeLayout erl_info = (ExpandableRelativeLayout) v.findViewById(R.id.erl_info);
        final Button bt_expand = (Button) v.findViewById(R.id.bt_expand);


        // button to collapse everything
        final ImageButton ib_collapse = (ImageButton) v.findViewById(R.id.ib_expand);

        hideKeyboard(getActivity());

        bt_expand.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                // Toggle the expandable view
                erl_info.toggle();
                bt_expand.setVisibility(v.GONE);
            }
        });

        ib_collapse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                // Toggle the expandable view
                erl_info.toggle();
                bt_expand.setVisibility(v.VISIBLE);

            }
        });

        // instantiate views in layout
        final EditText et_from = (EditText) v.findViewById(R.id.et_from);
        final EditText et_to = (EditText) v.findViewById(R.id.et_to);
        final EditText et_date = (EditText) v.findViewById(R.id.et_date);

        // BUTTON FOR DEMO
        TextView bt_ruben_demo_add = (TextView) v.findViewById(R.id.bt_ruben_demo_add);
        bt_ruben_demo_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_from.setText("San Antonio");
                et_to.setText("Seattle");
                et_date.setText("08/11/17");
                int month = Integer.parseInt(et_date.getText().toString().substring(0, 2));
                int dayOfMonth = Integer.parseInt(et_date.getText().toString().substring(3, 5));
                int year = Integer.parseInt(et_date.getText().toString().substring(6, 8)) + 2000;
            }
        });

        final ImageView iv_item = (ImageView) v.findViewById(R.id.iv_itemRequestedPhoto);
        tv_pending_counter = (TextView) v.findViewById(R.id.tv_pending_counter);
        tv_accepted_counter = (TextView) v.findViewById(R.id.tv_accepted_counter);

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
                hideKeyboard(getActivity());
                Intent i = new Intent(getContext(), SearchResultsActivity.class);
                /* inside of SearchResultsActivity we will call the database using the
                *from* *to* and *by* parameters typed in here, and there in SRA we will get
                back an array of TravelNotices to put into the recycler view */
                if (et_date.length() != 0 && et_from.length() != 0 && et_to.length() != 0) {
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
                else {Snackbar.make(getView(), String.format("Please make sure you've entered all the required information"), Snackbar.LENGTH_LONG).show();}
            }
        });

        // Calendar
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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
                hideKeyboard(getActivity());
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
        rv_pendingRequests.setNestedScrollingEnabled(false);
        // stuff for Accepted Requests
        mAcceptedRqs = new ArrayList<>();
        shippingAcceptedRequestsAdapter = new ShippingAcceptedRequestsAdapter(mAcceptedRqs);
        rv_acceptedRequests = (RecyclerView) v.findViewById(R.id.rv_accepted_requests);
        rv_acceptedRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_acceptedRequests.setAdapter(shippingAcceptedRequestsAdapter);
        rv_acceptedRequests.setNestedScrollingEnabled(false);

        // clear view button
        final TextView tv_clear_content = (TextView) v.findViewById(R.id.tv_clear_content);
        tv_clear_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearViews();
            }
        });

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

        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());

        client.get(RawrApp.DB_URL + "/request/get_my", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                shippingPendingRequestsAdapter.clear();
                shippingAcceptedRequestsAdapter.clear();
                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                    Log.e(TAG, "An error occurred while parsing the JSON Array");
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("[sr/get_my] CODE: %s ERROR(1): %s", statusCode, errorResponse));
                tv_accepted_counter.setVisibility(View.VISIBLE);
                tv_pending_counter.setVisibility(View.VISIBLE);
                String msg;
                try {
                    msg = errorResponse.getString("message");
                } catch (Exception e) {
                    Log.e(TAG, String.format("BAD ERROR IN TRY CATCH tn/get: %s", e));
                    msg = "Error (1) occurred while acquiring the requests sent to user";
                }
                // make a snackbar because of the error
                // TODO - This sometimes causes error, it says "java.lang.NullPointerException: Attempt to invoke virtual method 'void com.codepath.rawr.MainActivity.snackbarCallLong(java.lang.String)' on a null object"
                // TODO - Fix this error (next line that says: (MainActivity) getActivity()).snackbarCallLong(msg);
                ((MainActivity) getActivity()).snackbarCallLong(msg);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("error 3"));
                ((MainActivity) getActivity()).snackbarCallLong(String.format("Error(3) occurred %s", responseString));
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
                client.get(RawrApp.DB_URL + "/travel_notice/get", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            ShippingRequest shippingRequest = ShippingRequest.fromJSONServer(requestsList.getJSONObject(finalI), response.getJSONObject("data"), response.getJSONObject("user"));
                            // if it's accepted, add it to the accepted RV, otherwise add it to the pending RV
                            if (shippingRequest.isAccepted()) {
                                mAcceptedRqs.add(shippingRequest);
                                shippingAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRqs.size() - 1);
                                tv_accepted_counter.setVisibility(View.GONE);
                            } else if (shippingRequest.isPending()){
                                mPendingRqs.add(shippingRequest);
                                shippingPendingRequestsAdapter.notifyItemInserted(mPendingRqs.size() - 1);
                                tv_pending_counter.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, String.format("JSON error in parsing JSON in travel notice get: %s", e));
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, String.format("[tn/get] CODE: %s ERROR(1): %s", statusCode, errorResponse));
                        String msg;
                        try {
                            msg = errorResponse.getString("message");
                        } catch (Exception e) {
                            Log.e(TAG, String.format("BAD ERROR IN TRY CATCH tn/get: %s", e));
                            msg = "Error (1) occurred while acquiring the requests sent to user";
                        }
                        // make a snackbar because of the error
                        ((MainActivity) getActivity()).snackbarCallLong(msg);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, String.format("[tn/get] CODE: %s ERROR(3): %s", statusCode, responseString));
                        ((MainActivity) getActivity()).snackbarCallLong(String.format("Error(3) occurred %s", responseString));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("JSON Exception occurred in populateList at index %s: %s", i, e));
                ((MainActivity) getActivity()).snackbarCallLong(String.format("JSON Exception occurred in populateList at index %s: %s", i, e));
                Log.e(TAG, String.format("%s", e));
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
