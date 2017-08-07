package com.codepath.rawr.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.rawr.ProfileActivityOther;
import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.ReceiverFormActivity;
import com.codepath.rawr.SenderFormActivity;
import com.codepath.rawr.models.TravelNotice;
import com.codepath.rawr.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
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
 * Created by rdicker on 7/21/17.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private static final String TAG = "SearchResultAdapter";
    // declare variables
    private List<TravelNotice> mTrips;
    Context context;
    AsyncHttpClient client;

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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final TravelNotice trip = mTrips.get(position);
        // this will get the user and set his name in the given holder
        getUserAndPlaceNameInHolder(holder, trip.tuid);

        int num = position + 1;
        int total = mTrips.size();

        holder.tvResultNum.setText("Result " + num + " of " + total);

        holder.tv_from_isr.setText(trip.dep_iata);
        holder.tv_to_isr.setText(trip.arr_iata);
        holder.tv_dateFrom_isr.setText(trip.getDepartureDaySimple());
        holder.tv_dateTo_isr.setText(trip.getArrivalDaySimple());
        holder.tv_fromTime_isr.setText(trip.getDepartureTime());
        holder.tv_toTime_isr.setText(trip.getArrivalTime());
        holder.cb_envelope_isr.setChecked(trip.item_envelopes);
        holder.cb_largeBox_isr.setChecked(trip.item_lgbox);
        holder.cb_smallBox_isr.setChecked(trip.item_smbox);
        holder.cb_clothing_isr.setChecked(trip.item_clothing);
        holder.cb_other_isr.setChecked(trip.item_other);
        holder.cb_fragile_isr.setChecked(trip.item_fragile);
        holder.cb_liquids_isr.setChecked(trip.item_liquid);
        holder.cb_envelope_isr.setEnabled(false);
        holder.cb_largeBox_isr.setEnabled(false);
        holder.cb_smallBox_isr.setEnabled(false);
        holder.cb_clothing_isr.setEnabled(false);
        holder.cb_other_isr.setEnabled(false);
        holder.cb_fragile_isr.setEnabled(false);
        holder.cb_liquids_isr.setEnabled(false);
        holder.tv_dropoff.setText(trip.drop_off_flexibility);
        holder.tv_pickup.setText(trip.pick_up_flexibility);

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

        // get placeholder images
        Drawable profile_placeholder_loading = context.getDrawable(R.drawable.ic_profile_placeholder_loading);
        if (profile_placeholder_loading != null)  profile_placeholder_loading.setTint(context.getColor(R.color.White));
        Drawable profile_placeholder_error = context.getDrawable(R.drawable.ic_profile_placeholder_error);
        if (profile_placeholder_error != null) profile_placeholder_error.setTint(context.getColor(R.color.White));

        StorageReference ref = RawrApp.getStorageReferenceForImageFromFirebase(trip.tuid);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .bitmapTransform(new RoundedCornersTransformation(context, 20000, 0))
                .placeholder(profile_placeholder_loading)
                .error(profile_placeholder_error)
                .into(holder.iv_profileImageTraveller);

        holder.iv_profileImageTraveller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileActivityOther = new Intent(context, ProfileActivityOther.class);
                profileActivityOther.putExtra("user_id", trip.tuid);
                context.startActivity(profileActivityOther);
            }
        });
    }

    public void getUserAndPlaceNameInHolder(final ViewHolder holder, String user_id){
        /* Gets the user from the given id, then places his name on the holder.tv_travellerName */
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("uid", user_id);
        client.get(RawrApp.DB_URL + "/user/get", params, new JsonHttpResponseHandler() {
            User user;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = User.fromJSONServer(response.getJSONObject("data"));
                    holder.tv_travellerName.setText(user.fName + "'s trip");
                } catch (JSONException e) {
                    // error occurred, so we just log it
                    Log.e(TAG, String.format("JSON Error occurred: %s", e));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Server Error Received: %s", errorResponse));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvResultNum;
        public TextView tv_travellerName;
        public TextView tv_from_isr;
        public TextView tv_arrow_isr;
        public TextView tv_to_isr;
        public TextView tv_fromTime_isr;
        public TextView tv_toTime_isr;
        public TextView tv_dateFrom_isr;
        public TextView tv_dateTo_isr;

        public RelativeLayout rl_infoButton;

        final ImageView ivToggleInfo;
        final ExpandableRelativeLayout erl_info;

        public RelativeLayout rlCheckBoxes;
        public CheckBox cb_envelope_isr;
        public CheckBox cb_largeBox_isr;
        public CheckBox cb_smallBox_isr;
        public CheckBox cb_clothing_isr;
        public CheckBox cb_other_isr;
        public CheckBox cb_fragile_isr;
        public CheckBox cb_liquids_isr;

        public TextView tv_dropoff;
        public TextView tv_pickup;

        public Button btn_request_isr;

        public ImageView iv_profileImageTraveller;

        public ViewHolder(View itemView) {
            super(itemView);
            tvResultNum = (TextView) itemView.findViewById(R.id.tv_result_num);
            tv_travellerName = (TextView) itemView.findViewById(R.id.tv_travellerName);
            tv_arrow_isr = (TextView) itemView.findViewById(R.id.tv_arrow);
            tv_from_isr = (TextView) itemView.findViewById(R.id.tv_from);
            tv_to_isr = (TextView) itemView.findViewById(R.id.tv_to);
            tv_fromTime_isr = (TextView) itemView.findViewById(R.id.tv_fromTime);
            tv_toTime_isr = (TextView) itemView.findViewById(R.id.tv_toTime);
            tv_dateFrom_isr = (TextView) itemView.findViewById(R.id.tv_dateFrom);
            tv_dateTo_isr = (TextView) itemView.findViewById(R.id.tv_dateTo);

            rl_infoButton = (RelativeLayout) itemView.findViewById(R.id.rlCard);

            ivToggleInfo = (ImageView) itemView.findViewById(R.id.iv_toggleInfo);
            erl_info = (ExpandableRelativeLayout) itemView.findViewById(R.id.erl_info);

            rlCheckBoxes = (RelativeLayout) itemView.findViewById(R.id.rlCheckBoxes);
            cb_envelope_isr = (CheckBox) itemView.findViewById(R.id.cb_envelope);
            cb_largeBox_isr = (CheckBox) itemView.findViewById(R.id.cb_largeBox);
            cb_smallBox_isr = (CheckBox) itemView.findViewById(R.id.cb_smallBox);
            cb_clothing_isr = (CheckBox) itemView.findViewById(R.id.cb_clothing);
            cb_other_isr = (CheckBox) itemView.findViewById(R.id.cb_other);
            cb_fragile_isr = (CheckBox) itemView.findViewById(R.id.cb_fragile);
            cb_liquids_isr = (CheckBox) itemView.findViewById(R.id.cb_liquids);
            tv_dropoff = (TextView) itemView.findViewById(R.id.tv_dropoff);
            tv_pickup = (TextView) itemView.findViewById(R.id.tv_pickup);
            btn_request_isr = (Button) itemView.findViewById(R.id.btn_request);
            iv_profileImageTraveller = (ImageView) itemView.findViewById(R.id.iv_profileImageTraveller);

            btn_request_isr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you a sender or a receiver?")
                           /* .setTitle(R.string.dialog_title)  */   ;

                    builder.setPositiveButton("sender", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            Intent i = new Intent(context, SenderFormActivity.class);
                            i.putExtra("action", 0);
                            i.putExtra("travel_notice_id", mTrips.get(pos).id);
                            i.putExtra("ruid", RawrApp.getUsingUserId());
                            i.putExtra("tuid", mTrips.get(pos).tuid);
                            ((Activity) context).startActivityForResult(i, RawrApp.CODE_REQUESTER_FORMS_ACTIVITY);
                            // context.startActivity(i);
                        }
                    });
                    builder.setNegativeButton("receiver", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            Intent i = new Intent(context, ReceiverFormActivity.class);
                            i.putExtra("action", 1);
                            i.putExtra("travel_notice_id", mTrips.get(pos).id);
                            i.putExtra("ruid", RawrApp.getUsingUserId());
                            i.putExtra("tuid", mTrips.get(pos).tuid);
                            // converts context into activity and starts the activity for result
                            ((Activity) context).startActivityForResult(i, RawrApp.CODE_REQUESTER_FORMS_ACTIVITY);
                            // context.startActivity(i);
                        }
                    });

                    // show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
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
