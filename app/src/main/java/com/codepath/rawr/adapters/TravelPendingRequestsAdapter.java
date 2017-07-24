package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.R;
import com.codepath.rawr.models.ShippingRequest;

import java.util.List;

public class TravelPendingRequestsAdapter extends RecyclerView.Adapter<TravelPendingRequestsAdapter.ViewHolder> {

    // declare variables
    static private List<ShippingRequest> mRequests;
    Context context;

    public TravelPendingRequestsAdapter(List<ShippingRequest> requests) {
        mRequests = requests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View requestsView = inflater.inflate(R.layout.item_travel_pending_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(requestsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TravelPendingRequestsAdapter.ViewHolder holder, int position) {
        ShippingRequest singleRequest = mRequests.get(position);
//        holder.tv_from.setText(trips.dep_iata);
//        holder.tv_to.setText(trips.arr_iata);
//        holder.tv_dateFrom.setText(trips.getDepartureDaySimple());
//        holder.tv_dateTo.setText(trips.getArrivalDaySimple());
//        holder.tv_fromTime.setText(trips.getDepartureTime());
//        holder.tv_toTime.setText(trips.getArrivalTime());
//        holder.tv_airlineCode.setText(trips.airline);
//        holder.tv_airlineNo.setText(trips.flight_num);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relative_layout;
        public TextView tv_from;
        public TextView tv_to;
        public TextView tv_arrow;
        public TextView tv_fromTime;
        public TextView tv_toTime;
        public TextView tv_dateFrom;
        public TextView tv_dateTo;
        public TextView tv_item;
        public TextView tv_requester;
        public TextView tv_airlineCode;
        public TextView tv_airlineNo;

        public Button bt_accept;
        public Button bt_decline;

        public ViewHolder(View itemView) {
            super(itemView);

            relative_layout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
            tv_from = (TextView) itemView.findViewById(R.id.tv_from);
            tv_to = (TextView) itemView.findViewById(R.id.tv_to);
            tv_arrow = (TextView) itemView.findViewById(R.id.tv_arrow);
            tv_fromTime = (TextView) itemView.findViewById(R.id.tv_fromTime);
            tv_toTime = (TextView) itemView.findViewById(R.id.tv_toTime);
            tv_dateFrom = (TextView) itemView.findViewById(R.id.tv_dateFrom);
            tv_dateTo = (TextView) itemView.findViewById(R.id.tv_dateTo);

            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requester = (TextView) itemView.findViewById(R.id.tv_requester);
            tv_airlineCode = (TextView) itemView.findViewById(R.id.tv_airlineCode);
            tv_airlineNo = (TextView) itemView.findViewById(R.id.tv_airlineNo);

            bt_accept = (Button) itemView.findViewById(R.id.bt_accept);
            bt_decline = (Button) itemView.findViewById(R.id.bt_decline);
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
