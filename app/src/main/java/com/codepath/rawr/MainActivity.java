package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.rawr.adapters.MainPagerAdapter;
import com.codepath.rawr.fragments.SendReceiveFragment;
import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    // setting up the views pager for fragments
    public ViewPager vpPager; public MainPagerAdapter pagerAdapter; public CoordinatorLayout parentLayout; public TabLayout tabLayout;
    Context context;
    // other views
    ProgressBar pb;
    ImageView optionsButton;
    // db
    AsyncHttpClient client; public String[] DB_URLS;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref; SharedPreferences.Editor spEditor; String user_id;
    // debugging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);

        // get server stuffs
        client = new AsyncHttpClient(); DB_URLS = new String[] {getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};

        // get the views
        pb = (ProgressBar) findViewById(R.id.progressBarMainActivity);
        optionsButton = (ImageView) findViewById(R.id.optionsButton);

        setProgressVisible();
        // check if the user is logged in with the SharedPreferences, first get shared pref
        sharedPref = context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);
        checkIfUserLogged();


        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // create the pager adapter
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        // set the adapter for the pager
        vpPager.setAdapter(pagerAdapter);
        // setup the TabLayout to use the view pager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        // sets the tab icons
        setTabIcons();


        // TODO - Make option button actually do what it's supposed to do, include logout inside of it
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    public void setTabIcons() {
        /* this makes images bigger but causes some issues */
        View ic_flight = getLayoutInflater().inflate(R.layout.customtab, null);
        ic_flight.findViewById(R.id.iv_tab_icon).setBackgroundResource(R.drawable.ic_flight);
        tabLayout.getTabAt(0).setCustomView(ic_flight);
        View ic_suitcase = getLayoutInflater().inflate(R.layout.customtab, null);
        ic_suitcase.findViewById(R.id.iv_tab_icon).setBackgroundResource(R.drawable.ic_suitcase);
        tabLayout.getTabAt(1).setCustomView(ic_suitcase);
        View ic_chats = getLayoutInflater().inflate(R.layout.customtab, null);
        ic_chats.findViewById(R.id.iv_tab_icon).setBackgroundResource(R.drawable.ic_chats);
        tabLayout.getTabAt(2).setCustomView(ic_chats);
        /**/
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_flight);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_suitcase);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_chats);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vpPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.SXDark);
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                //tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        });
        vpPager.setCurrentItem(1);
    }

    public void checkIfUserLogged() {
        // get the id saved, if it's not saved, add, move to logoutActivitity to sign in or sign up
        user_id = sharedPref.getString(getString(R.string.sp_string_user_id_key), null);
        // if the user is empty, then we launch the logout activity because we know he's not logged in
        if (user_id == null) {
            launchLogoutActivity("You are not logged in. Please login or sign up.");
        } else {
            // otherwise sign user in
            signInUser(user_id);
        }
    }

    public void logoutUser() {
        /** This is for both debugging and for actually loging a user out. It unlogs the user and then make them log in again by
         * removing the shared preference */
        spEditor = sharedPref.edit();
        spEditor.remove(getString(R.string.sp_string_user_id_key));
        spEditor.apply();
        // force login, because user_id will be empty and this will launch logout
        checkIfUserLogged();
    }

    public void setProgressVisible() {
        pb.setVisibility(View.VISIBLE);
    }
    public void setProgressDead() {
        pb.setVisibility(View.GONE);
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

    /** on activity result for various things */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.ADDITIONAL_DETAILS_CODE) {
            // success snackbar
            snackbarCallLong("Your travel notice has been saved.");
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.ADDITIONAL_DETAILS_CODE) {
            // failure snackbar
            snackbarCallIndefinite(data.getStringExtra("message"));
        } else if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // success snackbar
            snackbarCallLong("Your request has been sent.");
            // clear the fragment texts and refresh requests
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).clearViews();
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).refreshRequests();
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // failure snackbar
            snackbarCallIndefinite(data.getStringExtra("message"));
        }
    }

    /**
     * All of the following are login logics
     * */
    public void launchLogoutActivity(String message) {
        setProgressDead();
        Intent logoutActivity = new Intent(MainActivity.this, LogoutActivity.class);
        logoutActivity.putExtra("message", message);
        startActivity(logoutActivity);
        // finishes this activity so that we don't go back to in onBackPressed
        MainActivity.this.finish();
    }

    public void signInUser(String id) {
        // make a https request for getting the user with this id
        RequestParams params = new RequestParams();
        params.put("uid", id);
        client.get(DB_URLS[0] + "/user_get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    loginUser();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Error while parsing JSON in sign in user."));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
                // fallback is to lauch logout activity (in case signing in doesn't work)
                launchLogoutActivity("It appears that your credentials are invalid. Please login or sign up.");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // fallback is to lauch logout activity (in case signing in doesn't work)
                launchLogoutActivity("It appears that your credentials are invalid. Please login or sign up.");
            }
        });
    }

    // make a login call to the database, this is practically the same as /user_get and is probably unnecessary... but lol.
    public void loginUser() {
        RequestParams params = new RequestParams();
        params.put("email", usingUser.email);
        client.get(DB_URLS[0] + "/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    updateUsingUserAndLauchMainActivity();
                    setProgressDead();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Error while parsing JSON in sign in user."));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
                // fallback is to lauch logout activity (in case signing in doesn't work)
                launchLogoutActivity("It appears that a server error occurred. Please login or sign up.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // fallback is to lauch logout activity (in case signing in doesn't work)
                launchLogoutActivity("It appears that a server error occurred. Please login or sign up.");
            }
        });
    }

    public void updateUsingUserAndLauchMainActivity(){
        try {
            setProgressVisible();
            // create the editor for shared preferences (this will be done in login or sign up activity, currently just for testing)
            spEditor = sharedPref.edit();
            spEditor.putString(getString(R.string.sp_string_user_id_key), usingUser.id);
            spEditor.commit();
            user_id = sharedPref.getString(getString(R.string.sp_string_user_id_key), null);
            Log.e(TAG, String.format("THIS: %s", user_id));
            RawrApp.setUsingUserId(usingUser.id);
            // we're currently in main activity so we make the progress bar invisible
            setProgressDead();
        } catch (Exception e) {
            e.printStackTrace();
            launchLogoutActivity("It appears that an error occurred with the id of the user. Please login or sign up.");
        }
    }

}