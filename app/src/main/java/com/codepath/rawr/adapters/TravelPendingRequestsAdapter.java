package com.codepath.rawr.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.TravelPendingRequestsActivity;
import com.codepath.rawr.models.ShippingRequest;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TravelPendingRequestsAdapter extends RecyclerView.Adapter<TravelPendingRequestsAdapter.ViewHolder> {

    // declare variables
    static private List<ShippingRequest> mRequests;
    Context context;
    AsyncHttpClient client;

    public final static String TAG = "TvlPendingRequestsAdapt";


    public TravelPendingRequestsAdapter(List<ShippingRequest> requests) {
        mRequests = requests;
        client = new AsyncHttpClient();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_pending_request, parent, false);

        return new ViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(TravelPendingRequestsAdapter.ViewHolder holder, int position) {
        final ShippingRequest request = mRequests.get(position);

        holder.tv_item.setText(request.getShippingItemName());
        holder.tv_requester.setText(request.getRequesterName());
//        holder.tv_date.setText(request.);

        // get placeholder images
        Drawable profile_placeholder_loading = context.getDrawable(R.drawable.ic_profile_placeholder_loading);
        if (profile_placeholder_loading != null) profile_placeholder_loading.setTint(context.getColor(R.color.PLight));
        Drawable profile_placeholder_error = context.getDrawable(R.drawable.ic_profile_placeholder_error);
        if (profile_placeholder_error != null) profile_placeholder_error.setTint(context.getColor(R.color.PLight));
        Drawable image_placeholder_loading = context.getDrawable(R.drawable.ic_image_placeholder_loading);
        if (image_placeholder_loading != null) image_placeholder_loading.setTint(context.getColor(R.color.PLight));
        Drawable image_placeholder_error = context.getDrawable(R.drawable.ic_image_placeholder_error);
        if (image_placeholder_error != null) image_placeholder_error.setTint(context.getColor(R.color.PLight));

        StorageReference ref = RawrApp.getStorageReferenceForImageFromFirebase(request.requesterId);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .bitmapTransform(new RoundedCornersTransformation(context, 20000, 0))
                .placeholder(profile_placeholder_loading)
                .error(profile_placeholder_error)
                .into(holder.iv_profile_image_requester);

        StorageReference refImageRequested = RawrApp.getStorageReferenceForImageFromFirebase(request.id);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(refImageRequested)
                .placeholder(image_placeholder_loading)
                .error(image_placeholder_error)
                .into(holder.iv_itemRequestedPhoto);

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

        public ImageView iv_profile_image_requester;
        public ImageView iv_itemRequestedPhoto;

        public Button ib_accept;
        public Button ib_decline;

        public ViewHolder(final View itemView) {
            super(itemView);

            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            tv_requester_title = (TextView) itemView.findViewById(R.id.tv_requester_title);
            tv_request_date = (TextView) itemView.findViewById(R.id.tv_request_date);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requester = (TextView) itemView.findViewById(R.id.tv_requester);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            ib_accept = (Button) itemView.findViewById(R.id.ib_accept);
            ib_decline = (Button) itemView.findViewById(R.id.ib_decline);

            iv_profile_image_requester = (ImageView) itemView.findViewById(R.id.iv_profile_image_requester);
            iv_itemRequestedPhoto = (ImageView) itemView.findViewById(R.id.iv_itemRequestedPhoto);

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

                    client.post(RawrApp.DB_URL + "/request/accept", params, new JsonHttpResponseHandler() {
                        // implement endpoint here
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                ShippingRequest newRequest = ShippingRequest.fromJSONServer(response.getJSONObject("request"), response.getJSONObject("travel_notice"), response.getJSONObject("user"));
                                mRequests.set(pos, newRequest);
                                mRequests.remove(pos);

                                Snackbar bar = Snackbar.make(itemView, "Accepted request from " + newRequest.getRequesterName(), Snackbar.LENGTH_LONG)
                                        .setAction("Dismiss", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // Handle user action
                                            }
                                        });

                                bar.show();

                                notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, String.format("error 1 %s", errorResponse));
                            String msg;
                            try {
                                msg = errorResponse.getString("message");
                            } catch (JSONException e) {
                                msg = "An error occurred from the server";
                            }
                            ((TravelPendingRequestsActivity) context).snackbarCallLong("ERROR: " + msg);
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

                    client.post(RawrApp.DB_URL + "/request/decline", params, new JsonHttpResponseHandler() {
                        // implement endpoint here
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            int status = mRequests.get(pos).status;
                            mRequests.remove(pos);
                            Snackbar bar = Snackbar.make(itemView, "Request declined", Snackbar.LENGTH_LONG);
                            Log.d(TAG, String.format("%s", "DECLINED! Status = " + status));
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e(TAG, String.format("error 1 %s", errorResponse));
                            String msg;
                            try {
                                msg = errorResponse.getString("message");
                            } catch (JSONException e) {
                                msg = "An error occurred from the server";
                            }
                            ((TravelPendingRequestsActivity) context).snackbarCallLong("ERROR: " + msg);
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
