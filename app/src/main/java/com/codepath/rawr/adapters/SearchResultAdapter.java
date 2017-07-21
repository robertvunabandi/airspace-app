package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.R;
import com.codepath.rawr.models.TravelNotice;

import java.util.List;

/**
 * Created by rdicker on 7/21/17.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {


    // declare variables
    private List<TravelNotice> mTrips;
    Context context;


    public SearchResultAdapter(List<TravelNotice> trips){
        mTrips = trips;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tripView = inflater.inflate(R.layout.item_search_result, parent, false);
        ViewHolder viewHolder = new ViewHolder(tripView) {
        };
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TravelNotice trip = mTrips.get(position);
    }



    @Override
    public int getItemCount() {
        return 0;
    }






    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tv_from_isr;
        public TextView tv_arrow_isr;
        public TextView tv_to_isr;
        public TextView tv_fromTime_isr;
        public TextView tv_toTime_isr;
        public TextView tv_dateFrom_isr;
        public TextView tv_dateTo_isr;

        // final ImageView ivToggleInfo;
        // final ExpandableRelativeLayout erl_info;

        public RelativeLayout rlCheckBoxes;
        public CheckBox cb_envelope_isr;
        public CheckBox cb_largeBox_isr;
        public CheckBox cb_smallBox_isr;
        public CheckBox cb_clothing_isr;
        public CheckBox cb_other_isr;

        public TextView tv_dropoff;
        public TextView tv_pickup;

        public Button btn_request_isr;
        public Button btn_askQ_isr;




        public ViewHolder(View itemView) {
            super(itemView);

        }
    }



    public void clear() {
        mTrips.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<TravelNotice> list) {
        mTrips.addAll(list);
        notifyDataSetChanged();
    }

}
