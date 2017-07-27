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
 * Created by mandaleeyp on 7/26/17.
 */

public class TravelAcceptedRequestsAdapter extends RecyclerView.Adapter<TravelAcceptedRequestsAdapter.ViewHolder> {

    public List<ShippingRequest> mRequests;
    Context context;

    public TravelAcceptedRequestsAdapter(List<ShippingRequest> requests) {
        mRequests = requests;
    }

    @Override
    public TravelAcceptedRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View requestView = inflater.inflate(R.layout.item_travel_accepted_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(requestView) {
        };
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TravelAcceptedRequestsAdapter.ViewHolder holder, int position) {

        ShippingRequest request = mRequests.get(position);

        holder.tv_from.setText(request.tvl.dep_iata);
        holder.tv_to.setText(request.tvl.arr_iata);
        holder.tv_dateFrom.setText(request.tvl.getDepartureDaySimple());
        holder.tv_dateTo.setText(request.tvl.getArrivalDaySimple());
        holder.tv_fromTime.setText(request.tvl.getDepartureTime());
        holder.tv_toTime.setText(request.tvl.getArrivalTime());

        holder.tv_airlineCode.setText(request.tvl.airline);
        holder.tv_airlineNo.setText(request.tvl.flight_num);
        holder.tv_item.setText(request.getShippingItemName());
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
        public TextView tv_airlineCode;
        public TextView tv_airlineNo;
        public TextView tv_itemTitle;
        public TextView tv_item;
        public TextView tv_requestDateTitle;
        public TextView tv_requested_date;

        public Button btn_contact;
        public Button btn_cancel;

        public ViewHolder(View itemView){
            super(itemView);

            tv_from = (TextView) itemView.findViewById(R.id.tv_from);
            tv_arrow = (TextView) itemView.findViewById(R.id.tv_arrow);
            tv_to = (TextView) itemView.findViewById(R.id.tv_to);
            tv_fromTime = (TextView) itemView.findViewById(R.id.tv_fromTime);
            tv_toTime = (TextView) itemView.findViewById(R.id.tv_toTime);
            tv_dateFrom = (TextView) itemView.findViewById(R.id.tv_dateFrom);
            tv_dateTo = (TextView) itemView.findViewById(R.id.tv_dateTo);

            tv_airlineCode = (TextView) itemView.findViewById(R.id.tv_airlineCode);
            tv_airlineNo = (TextView) itemView.findViewById(R.id.tv_airlineNo);
            tv_itemTitle = (TextView) itemView.findViewById(R.id.tv_itemTitle);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requestDateTitle = (TextView) itemView.findViewById(R.id.tv_requestDateTitle);
            tv_requested_date = (TextView) itemView.findViewById(R.id.tv_requested_date);

            btn_contact = (Button) itemView.findViewById(R.id.bt_contact);
            btn_cancel = (Button) itemView.findViewById(R.id.bt_cancel);

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

}
