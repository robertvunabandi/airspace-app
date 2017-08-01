package com.codepath.rawr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.R;
import com.codepath.rawr.models.RawrNotification;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;
/**
 * Created by robertvunabandi on 7/28/17.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    public List<RawrNotification> mNotifications;
    Context context;
    AsyncHttpClient client;

    public NotificationsAdapter(List<RawrNotification> notifications) { mNotifications = notifications; }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        client = new AsyncHttpClient();
        LayoutInflater inflater = LayoutInflater.from(context);
        View notification = inflater.inflate(R.layout.item_rawr_notification, parent, false);
        ViewHolder viewHolder = new ViewHolder(notification) {};
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RawrNotification ntc = mNotifications.get(position);
        holder.notificationMessage.setText(ntc.message);
        holder.dateReceived.setText(String.format("%s at %s", ntc.date.dateVerbose, ntc.date.getTime12format()));
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // create public views here to be populated in next Viewholder
        public TextView dateReceived;
        public TextView notificationMessage;
        public RelativeLayout parentItemNotification;
        public ViewHolder(View itemView){
            super(itemView);
            // get view by id
            dateReceived = (TextView) itemView.findViewById(R.id.tv_time);
            notificationMessage = (TextView) itemView.findViewById(R.id.tv_notification_message);
            parentItemNotification = (RelativeLayout) itemView.findViewById(R.id.rl_notificationItemInit);

            parentItemNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - implement notification actions
                }
            });

            /*
            TODO - Implement to get a notification detailed view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }); */
            /*
            TODO - Make a call to db to delete this notification
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            }); */
        }


    }

    public void clear() {
        mNotifications.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<RawrNotification> list) {
        mNotifications.addAll(list);
        notifyDataSetChanged();
    }
}
