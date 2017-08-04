package com.codepath.rawr.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rawr.AdditionalDetailsActivity;
import com.codepath.rawr.MainActivity;
import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.adapters.UpcomingTripAdapter;
import com.codepath.rawr.models.Flight;
import com.codepath.rawr.models.TravelNotice;
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


public class TravelFragment extends Fragment {

    // Database url
    public static String[] DB_URLS;

    // Base URL for API
    public final static String API_BASE_URL = "https://api.flightstats.com/flex/schedules/rest";
    // parameter name for API key
    public final static String APP_KEY_PARAM = "appKey";
    public final static String APP_ID_PARAM = "appId";

    // Code for on activity result
    public static final int ADDITIONAL_DETAILS_CODE = 0;

    // Declaring client
    AsyncHttpClient client;
    Flight flight;

    // Declaring variables for adding a trip
    private int flightYear;
    private int flightMonth;
    private int flightDay;

    // Tag for debugging
    public final static String TAG = "TravelFragment";

    // Declaring variables for list of trips
    UpcomingTripAdapter upcomingTripAdapter;
    ArrayList<TravelNotice> mTrips;
    RecyclerView rv_trips;

    // Declaring variables for list of accepted requests
//    TravelAcceptedRequestsAdapter travelAcceptedRequestsAdapter;
//    ArrayList<ShippingRequest> mAcceptedRequests;
//    RecyclerView rv_accepted_requests;

    SwipeRefreshLayout swipeContainer;
    ScrollView scrollView;


    public TravelFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new AsyncHttpClient();
        DB_URLS = new String[] {getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
//        getRequestId();
        getTripsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_travel, container, false);

        scrollView = (ScrollView) v.findViewById(R.id.scrollMePls);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        // Setting up view for adding trips
        Button btnSubmit = (Button) v.findViewById(R.id.bt_submit);

        // setting up the expandable layout for adding a trip
        final ExpandableRelativeLayout erl_info = (ExpandableRelativeLayout) v.findViewById(R.id.erl_info);
        final Button bt_expand = (Button) v.findViewById(R.id.bt_expand);
        final ImageButton ib_expand = (ImageButton) v.findViewById(R.id.ib_expand);


        bt_expand.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Toggle the expandable view
                erl_info.toggle();
                bt_expand.setVisibility(v.GONE);
            }
        });

        ib_expand.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Toggle the expandable view
                erl_info.toggle();
                bt_expand.setVisibility(v.VISIBLE);

            }
        });



        final EditText airlineCode = (EditText) v.findViewById(R.id.til_airlineCode);
        final EditText flightNumber = (EditText) v.findViewById(R.id.til_flightNumber);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTripsData();
                // Snackbar.make(getView(), String.format("Trips refreshed"), Snackbar.LENGTH_LONG).show();
//                getRequestId();
            }
        });

        final EditText et_date = (EditText) v.findViewById(R.id.et_date);
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

                flightYear = year;
                flightMonth = monthOfYear + 1; // Since month start at 0
                flightDay = dayOfMonth;
            }
        };

        // onClickListener for date
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // onClickListener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create URL
                String url = API_BASE_URL + "/v1/json/flight/" + airlineCode.getText() + "/" + flightNumber.getText() + "/departing/" + flightYear + "/" + flightMonth + "/" + flightDay;
                Log.e(TAG, url);
                // set request parameters
                RequestParams params = new RequestParams();
                params.put(APP_KEY_PARAM, getString(R.string.api_key)); // API key, always required
                params.put(APP_ID_PARAM, getString(R.string.app_id)); // AppId

                //execute a GET request expecting a JSON object response
                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            // get flight
                            flight = Flight.fromJSON(response);
                            // Log.d(TAG, String.format("%s", response)); // debugging to see response
                            processResponse(response);
                        } catch (JSONException e) {
                            Snackbar.make(getView(), String.format("Sorry, we couldn't find your flight. Please double check the information you entered"), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e(TAG, String.format("error 1: %s", errorResponse));
                        Snackbar.make(getView(), String.format("An error (1) occurred"), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e(TAG, String.format("error 2: %s", errorResponse));
                        Snackbar.make(getView(), String.format("An error (2) occurred"), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e(TAG, String.format("error 3: %s", responseString));
                        Snackbar.make(getView(), String.format("Make sure to include all of the required flight information!"), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        // setting up RecyclerView for list of trips
        mTrips = new ArrayList<>();
        upcomingTripAdapter = new UpcomingTripAdapter(mTrips);
        rv_trips = (RecyclerView) v.findViewById(R.id.rv_trips);
        rv_trips.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_trips.setAdapter(upcomingTripAdapter);
        rv_trips.setNestedScrollingEnabled(false);

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
        EditText til_airlineCode = (EditText) getView().findViewById(R.id.til_airlineCode);
        EditText til_flightNumber = (EditText) getView().findViewById(R.id.til_flightNumber);
        EditText et_date = (EditText) getView().findViewById(R.id.et_date);
        til_airlineCode.setText("");
        til_flightNumber.setText("");
        et_date.setText("");
    }

    // Process response for adding a trip
    public void processResponse(final JSONObject response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(flight.getAirlineName() + "\nFlight " + flight.getAirlineCode() + " " + flight.getFlightNumber() + "\n"
                + flight.getDepartureAirportCode() + " to " + flight.getArrivalAirportCode()
                + "\nDeparting on " + flight.getDepartFullDate() + " at " + flight.getDepartureTime())
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button, so send response to database

                // first, create the travelNotice, all surrounded by try catch
                try {
                    // creates a travel notice
                    final TravelNotice tvl = TravelNotice.fromJSON(response, RawrApp.getUsingUserId(), null, null);
                    // get parameters from the method createParams() in TravelNotice, see that method
                    RequestParams params = tvl.createParams();
                    // Send a request to the database with endpoint /travel_notice_add
                    client.post(DB_URLS[0] + "/travel_notice/add", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                if (!response.getBoolean("error")) {
                                    // in case of no error, pop up the addDetailsDialog that asks the user if they want to add more details
                                    addDetailsDialog(response.getJSONObject("data").getString("_id"), response.getJSONObject("data").getString("tuid"));
                                } else {
                                    // get the error from the DB
                                    String error = response.getJSONObject("message").toString();
                                    // if there is an internal db error that occurred, we handle it
                                    Toast.makeText(getContext(), String.format("An internal server error occurred: %s", error), Toast.LENGTH_LONG).show();
                                    // TODO - Handle what to do in case of an error from the DB. i.e.: tvl was not saved
                                    // Consider handling the case of the user having no internet connection
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                String toastMessage = "JSON Parsing error in onSuccess";
                                Log.e("TravelFragment", String.format("%s, %s", e, toastMessage));
                                Toast.makeText(getContext(), String.format("%s", toastMessage), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                            Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                            Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e(TAG, String.format("%s", responseString));
                            Toast.makeText(getContext(), String.format("error 3 %s", responseString), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    // Don't move forward if an error occurs
                    e.printStackTrace();
                    String toastMessage = "JSON Parsing error in client.post, BAD";
                    Log.e("TravelFragment", String.format("%s, %s", e.toString(), toastMessage));
                    Toast.makeText(getContext(), String.format("%s", toastMessage), Toast.LENGTH_LONG).show();
                }
            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // Dialog asking for additional details
    public void addDetailsDialog(final String travelNoticeId, final String tuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to add details to your travel notice?")
                .setTitle("Add more details");
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                additionalDetailsActivityLaunch(travelNoticeId, tuid);

                upcomingTripAdapter.notifyDataSetChanged();
                getTripsData();
                hideKeyboard(getActivity());
                Snackbar.make(getView(), String.format("Travel notice added!"), Snackbar.LENGTH_LONG).show();
                scrollView.setSmoothScrollingEnabled(true);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // TODO - Take the user to the upcoming section, which should be updated with all of this user's travel notices

                upcomingTripAdapter.notifyDataSetChanged();
                getTripsData();
                // hideKeyboard method is at the end of this class, method take from https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
                hideKeyboard(getActivity());

                Snackbar mySnackbar = Snackbar.make(getView(), String.format("Your trip was added!"), Snackbar.LENGTH_INDEFINITE);
                mySnackbar.setAction("ADD DETAILS", new AddDetailsListener(travelNoticeId, tuid));
                mySnackbar.setActionTextColor(getResources().getColor(R.color.PLight));
                mySnackbar.show();



                scrollView.setSmoothScrollingEnabled(true);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void additionalDetailsActivityLaunch(String travelNoticeId, String tuid) {
        Intent AdditionalDetailsActivity = new Intent(getContext(), AdditionalDetailsActivity.class);
        AdditionalDetailsActivity.putExtra("tuid", tuid);
        AdditionalDetailsActivity.putExtra("travel_notice_id", travelNoticeId);
        getActivity().startActivityForResult(AdditionalDetailsActivity, ADDITIONAL_DETAILS_CODE);
    }

    public void updateAdditionalDetailsActivityLaunch(String travelNoticeId, String tuid) {
        Intent AdditionalDetailsActivity = new Intent(getContext(), AdditionalDetailsActivity.class);
        AdditionalDetailsActivity.putExtra("tuid", tuid);
        AdditionalDetailsActivity.putExtra("travel_notice_id", travelNoticeId);
        getActivity().startActivityForResult(AdditionalDetailsActivity, RawrApp.UPDATE_ADDITIONAL_DETAILS_CODE);
    }

    // populate list of trips from JSON
    private void populateList(JSONArray travelNoticeList) {
        for (int i = 0; i < travelNoticeList.length(); i++) {
            try {
                TravelNotice travelNotice = TravelNotice.fromJSONServer(travelNoticeList.getJSONObject(i));
                mTrips.add(travelNotice);
                upcomingTripAdapter.notifyItemInserted(mTrips.size() - 1);
                // Toast.makeText(getContext(), String.format("%s", travelNotice), Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                Log.e(TAG, String.format("Error occurred in JSON parsing"));
                e.printStackTrace();
                Toast.makeText(getContext(), String.format("%s", e), Toast.LENGTH_LONG).show();
            }
        }
    }


//    // populate list of trips from just an ArrayList for autorefreshing
//    private void populateList(ArrayList<TravelNotice> travelNoticeList) {
//        for (int i = 0; i < travelNoticeList.size(); i++) {
//            mTrips.add(travelNoticeList.get(i));
//            upcomingTripAdapter.notifyItemInserted(mTrips.size() - 1);
//            // Toast.makeText(getContext(), String.format("%s", travelNotice), Toast.LENGTH_LONG).show();
//
//        }
//    }

    // get data for list of trips
    public void getTripsData() {
        // Set the request parameters
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());
        client.get(DB_URLS[0] + "/travel_notice/get_mine", params, new JsonHttpResponseHandler() {

            // implement endpoint here
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                upcomingTripAdapter.clear();
                try {
                    populateList(response.getJSONArray("data"));
                } catch (JSONException e) {
                }
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                    errorResponse) {
                Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
                String errorSnack;
                boolean idError = false;
                try {
                    errorSnack = String.format("Server error (code %s): %s", statusCode, errorResponse.getString("message"));
                    if (statusCode == 403) idError = true;
                } catch (JSONException e) {
                    errorSnack = "Error (1) occurred from Server.";
                }
                ((MainActivity) getActivity()).snackbarCallLong(errorSnack);
                if (idError) ((MainActivity) getActivity()).launchLogoutActivity("It appears that you are not logged in. Please log in or sign up.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                    throwable) {
                String errorSnack = String.format("Server error (3) (code %s): %s", statusCode, responseString);;
                ((MainActivity) getActivity()).snackbarCallIndefinite(errorSnack);
            }
        });
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

    // get list of request IDs & call on method to get list of requests
//    private void getRequestId() {
//
//        // Set the request parameters
//        RequestParams params = new RequestParams();
//        params.put("uid", RawrApp.getUsingUserId());
//
//        client.get(DB_URLS[0] + "/request_get_to_me", params, new JsonHttpResponseHandler() {
//            // implement endpoint here
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                travelPendingRequestsAdapter.clear();
//                try {
//                    getRequestData(response.getJSONArray("data"));
//                    Log.e(TAG, String.format("%s", response));
//                } catch (JSONException e) {
//
//                }
//                //swipeContainer.setRefreshing(false);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
//                    errorResponse) {
//                Log.e(TAG, String.format("%s", errorResponse));
//                Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
//                    throwable) {
//                Log.e(TAG, String.format("%s", responseString));
//
//                Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // method that gets data for pending requests
//    private void getRequestData(final JSONArray requestId) {
//
//        for (int i = 0; i < requestId.length(); i++) {
//            try {
//                // Set the request parameters
//                RequestParams params = new RequestParams();
//                params.put("request_id", requestId.getJSONObject(i).getString("request_id"));
//                client.get(DB_URLS[0] + "/request_get", params, new JsonHttpResponseHandler() {
//                    // implement endpoint here
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                             ShippingRequest sr = ShippingRequest.fromJSONServer(response.getJSONObject("request"), response.getJSONObject("travel_notice"));
//                            if (sr.isPending()) {
//                                mRequests.add(sr);
//                                travelPendingRequestsAdapter.notifyItemInserted(mRequests.size() - 1);
//                                Log.e(TAG, String.format("%s", sr ));
//                            } else if (sr.isAccepted()) {
//                                mAcceptedRequests.add(sr);
//                                travelAcceptedRequestsAdapter.notifyItemInserted(mAcceptedRequests.size() - 1);
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, String.format("JSON Exception at request_get request_id: %s", e));
//                        }
////                        swipeContainer.setRefreshing(false);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
//                            errorResponse) {
//                        Log.e(TAG, String.format("Error 1 %s", errorResponse));
//                        Toast.makeText(getContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray
//                            errorResponse) {
//                        Log.e(TAG, String.format("Error 2 %s", errorResponse));
//                        Toast.makeText(getContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
//                            throwable) {
//                        Log.e(TAG, String.format("Error 3 %s", responseString));
//                        Toast.makeText(getContext(), String.format("error 3"), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    // This is called from the snackbar after a user adds a travel notice but decides not to add additional details, but then presses "ADD DETAILS" inside of that initial snackbar
    public class AddDetailsListener implements View.OnClickListener{

        String tvlID;
        String tvlrID;

        public AddDetailsListener(String travelNoticeId, String tuid) {
            tvlID = travelNoticeId;
            tvlrID = tuid;
        }

        @Override
        public void onClick(View v) {
            // Code to send the user to  UpdateAdditionalDetailsActivity
            updateAdditionalDetailsActivityLaunch(tvlID, tvlrID);
            Snackbar.make(getView(), String.format("Travel notice updated!"), Snackbar.LENGTH_LONG).show();
        }
    }
}
