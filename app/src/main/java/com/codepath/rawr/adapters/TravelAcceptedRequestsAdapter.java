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
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

/**
 * Created by mandaleeyp on 7/26/17.
 */

public class TravelAcceptedRequestsAdapter extends RecyclerView.Adapter<TravelAcceptedRequestsAdapter.ViewHolder> {

    public List<ShippingRequest> mAcceptedRequests;
    Context context;
    AsyncHttpClient client;
    public String[] DB_URLS;

    public TravelAcceptedRequestsAdapter(List<ShippingRequest> requests) {
        mAcceptedRequests = requests;
        client = new AsyncHttpClient();
    }

//    @Override
//    public TravelAcceptedRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View requestView = inflater.inflate(R.layout.item_travel_accepted_request, parent, false);
//        ViewHolder viewHolder = new ViewHolder(requestView) {
//        };
//        return viewHolder;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_accepted_request, parent, false);
        DB_URLS = new String[]{context.getString(R.string.DB_HEROKU_URL), context.getString(R.string.DB_LOCAL_URL)};
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TravelAcceptedRequestsAdapter.ViewHolder holder, int position) {

        ShippingRequest request = mAcceptedRequests.get(position);
        holder.tv_item.setText(request.getShippingItemName());
        holder.tv_requester.setText(request.getRequesterName());
    }

    @Override
    public int getItemCount() {
        return mAcceptedRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_itemTitle;
        public TextView tv_item;
        public TextView tv_requester;
        public TextView tv_requestDateTitle;
        public TextView tv_requested_date;

        public Button btn_contact;
        public Button btn_cancel;

        public ViewHolder(View itemView){
            super(itemView);

            tv_itemTitle = (TextView) itemView.findViewById(R.id.tv_itemTitle);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requestDateTitle = (TextView) itemView.findViewById(R.id.tv_requestDateTitle);
            tv_requested_date = (TextView) itemView.findViewById(R.id.tv_requested_date);
            tv_requester = (TextView) itemView.findViewById(R.id.tv_requester);

            btn_contact = (Button) itemView.findViewById(R.id.bt_contact);
            btn_cancel = (Button) itemView.findViewById(R.id.bt_cancel);

        }

        public void clear() {
            mAcceptedRequests.clear();
            notifyDataSetChanged();
        }

        // Add a list of items -- change to type used
        public void addAll(List<ShippingRequest> list) {
            mAcceptedRequests.addAll(list);
            notifyDataSetChanged();
        }
    }

}
