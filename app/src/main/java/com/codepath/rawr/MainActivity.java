package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.codepath.rawr.adapters.MainPagerAdapter;
import com.codepath.rawr.fragments.SendReceiveFragment;

public class MainActivity extends AppCompatActivity {
    // setting up the vire pager for fragments
    public ViewPager vpPager;
    public MainPagerAdapter pagerAdapter;
    public CoordinatorLayout parentLayout;
    Context context;

    private static final String TAG = "MainActivity";
    private static final int CODE_SENDER_FORM_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);

        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // create the pager adapter
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        // set the adapter for the pager
        vpPager.setAdapter(pagerAdapter);
        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        // TODO - set the images of the fragments
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_android);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_android);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_android);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO - this snackbar is not displaying, fix this
        if (resultCode == RESULT_OK && requestCode == CODE_SENDER_FORM_ACTIVITY) {
            // success snackbar
            Snackbar.make(parentLayout, "Your request has been sent.", Snackbar.LENGTH_LONG).show();
            // TODO - uncomment the line below and implement "clearViews()" and "refreshPending()" methods
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).clearViews();
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).refreshRequests();
        } else if (resultCode == RESULT_CANCELED && requestCode == CODE_SENDER_FORM_ACTIVITY) {
            // failure snackbar
            Snackbar.make(parentLayout, String.format("THE FOLLOWING ERROR OCCURRED: %s", data.getStringExtra("message")), Snackbar.LENGTH_INDEFINITE).show();
            // clear the texts so that we're back to nothing
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).clearViews();
        }
    }
}