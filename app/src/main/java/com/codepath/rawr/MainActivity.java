package com.codepath.rawr;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.codepath.rawr.adapters.MainPagerAdapter;

public class MainActivity extends AppCompatActivity {
    public ViewPager vpPager;
    public View parentLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        parentLayout = findViewById(R.id.parentLayout);

        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the adapter for the pager
        vpPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), this));
        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        // TODO - set the images of the fragments
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_android);
    }
}