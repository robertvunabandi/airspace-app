package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.models.ShippingRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TravelPendingRequestsAdapter extends RecyclerView.Adapter<TravelPendingRequestsAdapter.ViewHolder> {

    // declare variables
    static private List<ShippingRequest> mRequests;
    Context context;
    AsyncHttpClient client;
    public String[] DB_URLS;

    public TravelPendingRequestsAdapter(List<ShippingRequest> requests) {
        mRequests = requests;
        client = new AsyncHttpClient();
        RawrApp.getUsingUserId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View requestsView = inflater.inflate(R.layout.item_travel_pending_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(requestsView);
        DB_URLS = new String[]{context.getString(R.string.DB_HEROKU_URL), context.getString(R.string.DB_LOCAL_URL)};
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TravelPendingRequestsAdapter.ViewHolder holder, int position) {
        ShippingRequest singleRequest = mRequests.get(position);
        holder.tv_from.setText(singleRequest.tvl.dep_iata);
        holder.tv_to.setText(singleRequest.tvl.arr_iata);
        holder.tv_dateFrom.setText(singleRequest.tvl.getDepartureDaySimple());
        holder.tv_dateTo.setText(singleRequest.tvl.getArrivalDaySimple());
        holder.tv_fromTime.setText(singleRequest.tvl.getDepartureTime());
        holder.tv_toTime.setText(singleRequest.tvl.getArrivalTime());
        holder.tv_airlineCode.setText(singleRequest.tvl.airline);
        holder.tv_airlineNo.setText(singleRequest.tvl.flight_num);
        holder.tv_item.setText(singleRequest.getShippingItemName());
        holder.tv_requester.setText(singleRequest.getRequesterName());

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // view holder for trip data
        public RelativeLayout relative_layout;
        public TextView tv_from;
        public TextView tv_to;
        public TextView tv_arrow;
        public TextView tv_fromTime;
        public TextView tv_toTime;
        public TextView tv_dateFrom;
        public TextView tv_dateTo;
        public TextView tv_airlineCode;
        public TextView tv_airlineNo;

        // view holder for shipping request data
        public TextView tv_item;
        public TextView tv_requester;

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




            // ACCEPT the request click listener
            bt_accept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    RequestParams params = new RequestParams();
                    params.put("travel_notice_id", mRequests.get(pos).tvl.id);
                    params.put("request_id",  mRequests.get(pos).id);
                    params.put("traveler_id",  mRequests.get(pos).tvl.tuid);

                    client.post(DB_URLS[0] + "/request_accept", params, new JsonHttpResponseHandler() {
                        // implement endpoint here
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                ShippingRequest newRequest = ShippingRequest.fromJSONServer(response.getJSONObject("request"), response.getJSONObject("travel_notice"));
                                mRequests.set(pos, newRequest);
                                Toast.makeText(context, String.format("%s", "STATUS = " + newRequest.status + "    " + response), Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

            // DECLINE the request click listener
            bt_decline.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    RequestParams params = new RequestParams();
                    params.put("travel_notice_id", mRequests.get(pos).tvl.id);
                    params.put("request_id",  mRequests.get(pos).id);
                    params.put("traveler_id",  mRequests.get(pos).tvl.tuid);

                    client.post(DB_URLS[0] + "/request_decline", params, new JsonHttpResponseHandler() {
                        // implement endpoint here
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            int status = mRequests.get(pos).status;
                            mRequests.remove(pos);
                            Toast.makeText(context, String.format("%s", "DECLINED! Status = " + status), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
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
