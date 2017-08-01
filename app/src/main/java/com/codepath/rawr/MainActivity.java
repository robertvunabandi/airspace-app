package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.rawr.adapters.MainPagerAdapter;
import com.codepath.rawr.fragments.SendReceiveFragment;
import com.codepath.rawr.fragments.TravelFragment;
import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;

public class MainActivity extends AppCompatActivity {
    // setting up the views pager for fragments
    public ViewPager vpPager;
    public MainPagerAdapter pagerAdapter;
    public CoordinatorLayout parentLayout;
    public TabLayout tabLayout;
    Context context;
    // other views
    ProgressBar pb;
    ImageView optionsButton;
    // db
    AsyncHttpClient client;
    public String[] DB_URLS;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref;
    SharedPreferences.Editor spEditor;
    String user_id;
    // debugging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);

        // get server stuffs
        client = new AsyncHttpClient();
        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};

        // get the views
        pb = (ProgressBar) findViewById(R.id.progressBarMainActivity);
        optionsButton = (ImageView) findViewById(R.id.optionsButton);

        // check if the user is logged in with the SharedPreferences, first get shared pref
        sharedPref = context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);

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
        ((ImageView) ic_flight.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight);
        ((TextView) ic_flight.findViewById(R.id.tv_text_icon)).setText(getString(R.string.travel));
        tabLayout.getTabAt(0).setCustomView(ic_flight);
        View ic_suitcase = getLayoutInflater().inflate(R.layout.customtab, null);
        ((ImageView) ic_suitcase.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase);
        ((TextView) ic_suitcase.findViewById(R.id.tv_text_icon)).setText(getString(R.string.send_receive));
        tabLayout.getTabAt(1).setCustomView(ic_suitcase);
        View ic_chats = getLayoutInflater().inflate(R.layout.customtab, null);
        ((ImageView) ic_chats.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats);
        ((TextView) ic_chats.findViewById(R.id.tv_text_icon)).setText(getString(R.string.chats));
        tabLayout.getTabAt(2).setCustomView(ic_chats);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vpPager) {
            public void changeColorTab(TabLayout.Tab tab, int color) {
                // color 0: dark, 1: white
                int white = ContextCompat.getColor(context, R.color.White);
                int dark = ContextCompat.getColor(context, R.color.SXDark);

                // ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                // logic to change the background to white or black
                Drawable bkg = tab.getCustomView().findViewById(R.id.iv_tab_icon).getBackground();
                if (color == 1) {
                    // ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).getDrawable().setTint(white);
                    if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_flight).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight_white);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_suitcase).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase_white);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_chats).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats_white);
                    }
                    ((TextView) tab.getCustomView().findViewById(R.id.tv_text_icon)).setTextColor(white);
                } else {
                    if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_flight_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_suitcase_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_chats_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats);
                    }
                    ((TextView) tab.getCustomView().findViewById(R.id.tv_text_icon)).setTextColor(dark);
                }
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                changeColorTab(tab, 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.SXDark);
                changeColorTab(tab, 0);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                changeColorTab(tab, 1);
            }
        });
        vpPager.setCurrentItem(1);
    }

    public void logoutUser() {
        /** This is for both debugging and for actually loging a user out. It unlogs the user and then make them log in again by
         * removing the shared preference */
        spEditor = sharedPref.edit();
        spEditor.remove(getString(R.string.sp_string_user_id_key));
        spEditor.apply();
        // log the user out
        launchLogoutActivity(null);
    }

    public void setProgressVisible() {
        pb.setVisibility(View.VISIBLE);
    }

    public void setProgressDead() {
        pb.setVisibility(View.GONE);
    }

    // Snackbar calls
    public void snackbarCall(String message, int length) {
        Snackbar.make(parentLayout, String.format("%s", message), length).show();
    }
    public void snackbarCallIndefinite(String message) {
        snackbarCall(message, Snackbar.LENGTH_INDEFINITE);
    }
    public void snackbarCallLong(String message) {
        snackbarCall(message, Snackbar.LENGTH_LONG);
    }
    public void snackbarCallShort(String message) {
        snackbarCall(message, Snackbar.LENGTH_SHORT);
    }

    /**
     * on activity result for various things
     */
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
        } else if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            ((TravelFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).getTripsData();
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            snackbarCallIndefinite(data.getStringExtra("message"));
        }
    }

    /**
     * All of the following are login logics
     */
    public void launchLogoutActivity(String message) {
        setProgressDead();
        Intent logoutActivity = new Intent(MainActivity.this, LogoutActivity.class);
        logoutActivity.putExtra("message", message);
        startActivity(logoutActivity);
        // finishes this activity so that we don't go back to in onBackPressed
        MainActivity.this.finishAffinity();
    }
}