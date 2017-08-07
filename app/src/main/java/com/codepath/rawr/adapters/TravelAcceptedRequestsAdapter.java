package com.codepath.rawr.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.codepath.rawr.TravelAcceptedRequestsActivity;
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

/**
 * Created by mandaleeyp on 7/26/17.
 */

public class TravelAcceptedRequestsAdapter extends RecyclerView.Adapter<TravelAcceptedRequestsAdapter.ViewHolder> {

    public List<ShippingRequest> mAcceptedRequests;
    Context context;
    AsyncHttpClient client;
    public String[] DB_URLS;
    public static final String TAG = "TvlAccReqAdap";


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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final TravelAcceptedRequestsAdapter.ViewHolder holder, final int position) {

        final ShippingRequest request = mAcceptedRequests.get(position);
        holder.tv_item.setText(request.getShippingItemName());
        holder.tv_requester.setText(request.getRequesterName());
        // holder.tv_requested_date.setText(RawrDate.simpleDateFromDDMMYYYY(request.dateCreated.day, request.dateCreated.month, request.dateCreated.year));

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

        // TODO - SHOULD THERE EVEN BE A CANCEL BUTTON?
        // CANCEL the request click listener
        holder.btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {

                // They have to confirm that they want to cancel the request
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to cancel this request?");

                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "YES" button, so send response to database

                        RequestParams params = new RequestParams();
                        params.put("uid",  request.requesterId);
                        params.put("request_id",  request.id);
                        client.post(RawrApp.DB_URL + "/request/delete", params, new JsonHttpResponseHandler() {
                            // implement endpoint here
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                int status = request.status;
                                mAcceptedRequests .remove(position);
                                Snackbar.make(v, String.format("Request cancelled"), Snackbar.LENGTH_LONG).show();
                                notifyDataSetChanged();

                                // TODO - notify the traveller that the shipper cancelled the request!!!! (if not done in the database already)
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
                                ((TravelAcceptedRequestsActivity) context).snackbarCallLong("ERROR: " + msg);
                            }
                        });
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // CONTACT the traveler click listener
        holder.btn_contact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Get the layout inflater
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View vi = li.inflate(R.layout.dialog_contact_info, null, false);
                TextView title = (TextView) vi.findViewById(R.id.dialogTitle);
                TextView tvlrPhone = (TextView) vi.findViewById(R.id.tv_tvlr_phone);
                TextView tvlrEmail = (TextView) vi.findViewById(R.id.tv_tvlr_email);
                title.setText(request.recipient.name + "'s contact information");
                tvlrPhone.setText(request.recipient.phone);
                tvlrEmail.setText(request.recipient.email);
                builder.setView(vi);

                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "YES" button, so send response to database

                        RequestParams params = new RequestParams();
                        params.put("uid",  request.requesterId);
                        params.put("request_id",  request.id);
                        client.post(RawrApp.DB_URL + "/request/delete", params, new JsonHttpResponseHandler() {
                            // implement endpoint here
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                int status = request.status;
                                mAcceptedRequests .remove(position);
                                Snackbar.make(v, String.format("Request cancelled"), Snackbar.LENGTH_LONG).show();
                                notifyDataSetChanged();

                                // TODO - notify the traveller that the shipper cancelled the request!!!! (if not done in the database already)
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
                                ((TravelAcceptedRequestsActivity) context).snackbarCallLong("ERROR: " + msg);
                            }
                        });
                    }
                });
                builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.bt_deliver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Get the layout inflater
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View vi = li.inflate(R.layout.dialog_confirm_delivery, null, false);
                TextView title = (TextView) vi.findViewById(R.id.dialogTitle);
                title.setText("Are you sure you want to confirm that you completed " + request.tvlUser.fName + "'s delivery?");
                builder.setView(vi);

                // Add the buttons
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "YES"
                        holder.bt_deliver.setText("DELIVERED ✓");
                        holder.bt_deliver.setEnabled(false);
                        holder.btn_cancel.setEnabled(false);
                    }
                });

                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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

        public ImageView iv_profile_image_requester;
        public ImageView iv_itemRequestedPhoto;

        public Button btn_contact;
        public Button btn_cancel;
        public Button bt_deliver;

        public ViewHolder(View itemView){
            super(itemView);

            tv_itemTitle = (TextView) itemView.findViewById(R.id.tv_itemTitle);
            tv_item = (TextView) itemView.findViewById(R.id.tv_item);
            tv_requestDateTitle = (TextView) itemView.findViewById(R.id.tv_requestDateTitle);
            tv_requested_date = (TextView) itemView.findViewById(R.id.tv_requested_date);
            tv_requester = (TextView) itemView.findViewById(R.id.tv_requester);

            iv_profile_image_requester = (ImageView) itemView.findViewById(R.id.iv_profile_image_requester);
            iv_itemRequestedPhoto = (ImageView) itemView.findViewById(R.id.iv_itemRequestedPhoto);

            btn_contact = (Button) itemView.findViewById(R.id.bt_contact);
            btn_cancel = (Button) itemView.findViewById(R.id.bt_cancel);
            bt_deliver = (Button) itemView.findViewById(R.id.bt_deliver);

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
