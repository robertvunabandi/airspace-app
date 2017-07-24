package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.rawr.R;
import com.codepath.rawr.models.ShippingRequest;

import java.util.List;

/**
 * Created by rdicker on 7/24/17.
 */

public class ShippingAcceptedRequestsAdapter extends RecyclerView.Adapter<ShippingAcceptedRequestsAdapter.ViewHolder>{

    public List<ShippingRequest> mRequests;
    Context context;

    public ShippingAcceptedRequestsAdapter(List<ShippingRequest> requests){
        mRequests = requests;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View requestView = inflater.inflate(R.layout.item_shipping_accepted_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(requestView) {
        };
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ShippingRequest request = mRequests.get(position);
//
//        holder.tv_from.setText(request.trip.dep_iata);
//        holder.tv_to.setText(request.trip.arr_iata);
//        holder.tv_dateFrom.setText(request.trip.getDepartureDaySimple());
//        holder.tv_dateTo.setText(request.trip.getArrivalDaySimple());
//        holder.tv_fromTime.setText(request.trip.getDepartureTime());
//        holder.tv_toTime.setText(request.trip.getArrivalTime());

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }








    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView tv_from;
        public TextView tv_arrow;
        public TextView tv_to;
        public TextView tv_fromTime;
        public TextView tv_toTime;
        public TextView tv_dateFrom;
        public TextView tv_dateTo;
//
//        final ImageView ivToggleInfo;
//        final ExpandableRelativeLayout erl_info;
//
//        public RelativeLayout rlCheckBoxes;
//        public CheckBox cb_envelope_isr;
//        public CheckBox cb_largeBox_isr;
//        public CheckBox cb_smallBox_isr;
//        public CheckBox cb_clothing_isr;
//        public CheckBox cb_other_isr;
//
//        public TextView tv_dropoff;
//        public TextView tv_pickup;

        public Button btn_contact;
        public Button btn_cancel;

        public ViewHolder(View itemView){
            super(itemView);

            tv_from = (TextView) itemView.findViewById(R.id.tv_from);
            tv_to = (TextView) itemView.findViewById(R.id.tv_to);
            tv_arrow = (TextView) itemView.findViewById(R.id.tv_arrow);
            tv_fromTime = (TextView) itemView.findViewById(R.id.tv_fromTime);
            tv_toTime = (TextView) itemView.findViewById(R.id.tv_toTime);
            tv_dateFrom = (TextView) itemView.findViewById(R.id.tv_dateFrom);
            tv_dateTo = (TextView) itemView.findViewById(R.id.tv_dateTo);

            btn_contact = (Button) itemView.findViewById(R.id.bt_contact);
            btn_cancel = (Button) itemView.findViewById(R.id.bt_cancel);
        }

    }



    public void clear() {
        mRequests.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<ShippingRequest> list) {
        mRequests.addAll(list);
        notifyDataSetChanged();
    }
}
