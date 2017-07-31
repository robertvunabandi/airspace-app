package com.codepath.rawr.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rawr.R;
import com.codepath.rawr.models.ShippingRequest;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.rawr.fragments.TravelFragment.DB_URLS;

/**
 * Created by rdicker on 7/24/17.
 */

public class ShippingAcceptedRequestsAdapter extends RecyclerView.Adapter<ShippingAcceptedRequestsAdapter.ViewHolder>{

    public List<ShippingRequest> mRequests;
    Context context;
    AsyncHttpClient client;

    public ShippingAcceptedRequestsAdapter(List<ShippingRequest> requests){
        mRequests = requests;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        client = new AsyncHttpClient();
        LayoutInflater inflater = LayoutInflater.from(context);

        View requestView = inflater.inflate(R.layout.item_shipping_accepted_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(requestView) {
        };
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

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
        // holder.tv_requested_date.setText(request.tvl.getArrivalTime());

        holder.cb_envelope.setChecked(request.item_envelopes);
        holder.cb_largeBox.setChecked(request.item_lgbox);
        holder.cb_smallBox.setChecked(request.item_smbox);
        holder.cb_clothing.setChecked(request.item_clothing);
        holder.cb_other.setChecked(request.item_other);
        holder.cb_fragile.setChecked(request.item_fragile);
        holder.cb_liquids.setChecked(request.item_liquid);
        holder.cb_envelope.setEnabled(false);
        holder.cb_largeBox.setEnabled(false);
        holder.cb_smallBox.setEnabled(false);
        holder.cb_clothing.setEnabled(false);
        holder.cb_other.setEnabled(false);
        holder.cb_fragile.setEnabled(false);
        holder.cb_liquids.setEnabled(false);
        holder.tv_dropoff.setText(request.tvl.drop_off_flexibility);
        holder.tv_pickup.setText(request.tvl.pick_up_flexibility);
        holder.rl_infoButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                // Toggle the expandable view
                holder.erl_info.toggle();

                // Rotates the toggle button to indicate when the expandableLayout is either expanded or collapsed
                if (holder.erl_info.isExpanded()) {
                    holder.ivToggleInfo.setRotation(0);
                }
                else {
                    holder.ivToggleInfo.setRotation(-90);
                }

                // TODO - Add filters in XML
            }
        });

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

        public RelativeLayout rlChecks;
        public RelativeLayout rl_infoButton;
        public TextView tv_infoTitle;
        final ImageView ivToggleInfo;
        final ExpandableRelativeLayout erl_info;

        public RelativeLayout rlCheckBoxes;
        public CheckBox cb_envelope;
        public CheckBox cb_largeBox;
        public CheckBox cb_smallBox;
        public CheckBox cb_clothing;
        public CheckBox cb_other;
        public CheckBox cb_fragile;
        public CheckBox cb_liquids;

        public RelativeLayout rl_flexibility;
        public TextView tv_dropoff;
        public TextView tv_pickup;

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

            rlChecks = (RelativeLayout) itemView.findViewById(R.id.rlChecks);
            rl_infoButton = (RelativeLayout) itemView.findViewById(R.id.rl_infoButton);
            tv_infoTitle = (TextView) itemView.findViewById(R.id.tv_infoTitle);

            ivToggleInfo = (ImageView) itemView.findViewById(R.id.iv_toggleInfo);
            erl_info = (ExpandableRelativeLayout) itemView.findViewById(R.id.erl_info);

            rlCheckBoxes = (RelativeLayout) itemView.findViewById(R.id.rlCheckBoxes);
            cb_envelope = (CheckBox) itemView.findViewById(R.id.cb_envelope);
            cb_largeBox = (CheckBox) itemView.findViewById(R.id.cb_largeBox);
            cb_smallBox = (CheckBox) itemView.findViewById(R.id.cb_smallBox);
            cb_clothing = (CheckBox) itemView.findViewById(R.id.cb_clothing);
            cb_other = (CheckBox) itemView.findViewById(R.id.cb_other);
            cb_fragile = (CheckBox) itemView.findViewById(R.id.cb_fragile);
            cb_liquids = (CheckBox) itemView.findViewById(R.id.cb_liquids);

            tv_dropoff = (TextView) itemView.findViewById(R.id.tv_dropoff);
            tv_pickup = (TextView) itemView.findViewById(R.id.tv_pickup);




            // CANCEL the request click listener
            btn_cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    RequestParams params = new RequestParams();
                    params.put("uid",  mRequests.get(pos).requesterId);
                    params.put("request_id",  mRequests.get(pos).id);

                    client.post(DB_URLS[0] + "/request_delete", params, new JsonHttpResponseHandler() {
                        // implement endpoint here
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            int status = mRequests.get(pos).status;
                            mRequests.remove(pos);
                            Toast.makeText(context, String.format("%s", "CANCELLED! Status = " + status), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();

                            // TODO - notify the traveller that the shipper cancelled the request!!!!
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(context, String.format("error 1 %s", errorResponse), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Toast.makeText(context, String.format("error 2 %s", errorResponse), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(context, String.format("error 3"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

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
