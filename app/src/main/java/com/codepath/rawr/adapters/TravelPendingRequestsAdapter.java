package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rawr.R;
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
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View requestsView = inflater.inflate(R.layout.item_travel_pending_request, parent, false);
//        ViewHolder viewHolder = new ViewHolder(requestsView);
//        DB_URLS = new String[]{context.getString(R.string.DB_HEROKU_URL), context.getString(R.string.DB_LOCAL_URL)};
//        return viewHolder;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_pending_request, parent, false);
        DB_URLS = new String[]{context.getString(R.string.DB_HEROKU_URL), context.getString(R.string.DB_LOCAL_URL)};
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TravelPendingRequestsAdapter.ViewHolder holder, int position) {
        ShippingRequest request = mRequests.get(position);

        holder.tv_item.setText(request.getShippingItemName());
        holder.tv_requester.setText(request.getRequesterName());
//        holder.tv_date.setText(request.);

    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        // view holder for shipping request data
        public TextView tv_item_title;
        public TextView tv_requester_title;
        public TextView tv_request_date;
        public TextView tv_item;
        public TextView tv_requester;
        public TextView tv_date;

        public ImageButton ib_accept;
        public ImageButton ib_decline;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            tv_requester_title = (TextView) itemView.findViewById(R.id.tv_requester_title);
            tv_request_date = (TextView) itemView.findViewById(R.id.tv_request_date);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requester = (TextView) itemView.findViewById(R.id.tv_requester);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            ib_accept = (ImageButton) itemView.findViewById(R.id.ib_accept);
            ib_decline = (ImageButton) itemView.findViewById(R.id.ib_decline);

            // ACCEPT the request click listener
            ib_accept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();

                    RequestParams params = new RequestParams();
                    params.put("travel_notice_id", mRequests.get(pos).tvl.id);
                    params.put("request_id",  mRequests.get(pos).id);
                    params.put("traveler_id",  mRequests.get(pos).tvl.tuid);

                    // client was declared and instantiated in constructor for TravelPendingRequestsAdapter, not ViewHolder

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

//            // DECLINE the request click listener
            ib_decline.setOnClickListener(new View.OnClickListener(){
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
