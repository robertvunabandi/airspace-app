package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.rawr.fragments.ConversationsFragment;
import com.codepath.rawr.fragments.SendReceiveFragment;
import com.codepath.rawr.fragments.TravelFragment;

/**
 * Created by robertvunabandi on 7/20/17.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{ "Travel", "Send/Receive", "Messages"};
    private Context context;

    public TravelFragment travelFragment;
    public SendReceiveFragment sendReceiveFragment;
    public ConversationsFragment conversationsFragment;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        travelFragment = new TravelFragment();
        sendReceiveFragment = new SendReceiveFragment();
        conversationsFragment = new ConversationsFragment();
    }
    // return the total # of fragments

    @Override
    public int getCount() {
        return 3;
    }


    // return the fragment to use depending on the position

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
             return travelFragment;
        } else if (position == 1) {
            return sendReceiveFragment;
        } else if (position == 2){
            return conversationsFragment;
        } else {
            return null;
        }
    }

    // return title
    public CharSequence getPageTitle(int position) {
        // generate title based on item position
        return tabTitles[position];
    }
}
