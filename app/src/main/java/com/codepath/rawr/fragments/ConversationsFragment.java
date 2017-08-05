package com.codepath.rawr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.codepath.rawr.MainActivity;
import com.codepath.rawr.R;
import com.codepath.rawr.RawrApp;
import com.codepath.rawr.adapters.NotificationsAdapter;
import com.codepath.rawr.models.RawrNotification;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ConversationsFragment extends Fragment {
    public static final String TAG = "ConversationsFragment";
    // variables for HTTP calls
    AsyncHttpClient client;
    public int NOTIFICATION_COUNT = 0;

    /* Declaring variables for messages TODO - Add these stuffs when we add messages
    ConversationListAdapter conversationListAdapter;
    ArrayList(Messages) mMessages;
    RecyclerView rv_convos; */

    // variables for notifications
    NotificationsAdapter notificationsAdapter;
    public ArrayList<RawrNotification> mNotifications;
    SwipeRefreshLayout swipeContainer;
    RecyclerView rv_notifications;
    JSONArray notificationsArray;
    ItemTouchHelper.SimpleCallback swipeDeleteItemNotificationCallback;
    ItemTouchHelper itemTouchHelperNotification;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new AsyncHttpClient();
        // call to get the notifications after the stuff has been created
        getNotifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        /* swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });
        swipeContainer.setEnabled(false); */

        // populate the recycler view of notifcation with notifications from the server
        rv_notifications = (RecyclerView) v.findViewById(R.id.rv_notifications);
        mNotifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(mNotifications);
        rv_notifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_notifications.setAdapter(notificationsAdapter);
        rv_notifications.setNestedScrollingEnabled(false);
        client = new AsyncHttpClient();

        /* TODO - If we add messages, we need this
        // populate the recycler view of messages with messages from the server
        rv_convos = (RecyclerView) v.findViewById(rv_convos);
        mMessages = new ArrayList<>();
        conversationListAdapter = new ConversationListAdapter(mMessages);
        rv_convos.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_convos.setAdapter(conversationListAdapter);
        rv_convos.setNestedScrollingEnabled(false);
        */

        enableSwipeToDelete();

        return v;
    }


    public void enableSwipeToDelete() {
        swipeDeleteItemNotificationCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // remove swiped item from list and notify the Recycler view
                int position = viewHolder.getAdapterPosition();
                RawrNotification nf = mNotifications.get(position);
                removeNotification(nf);
                mNotifications.remove(position);
                notificationsAdapter.notifyDataSetChanged();
                // set to false to subtract to the notification indicator counter by 1
                UpdateConversationsNotificationIndicator(false);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                final boolean swiping = actionState == ItemTouchHelper.ACTION_STATE_SWIPE;
                // swipeContainer.setEnabled(!swiping);
            }
        };
        itemTouchHelperNotification = new ItemTouchHelper(swipeDeleteItemNotificationCallback);
        itemTouchHelperNotification.attachToRecyclerView(rv_notifications);
    }



    public void getNotifications() {
        // gets the notifications from the server and then makes a call to populate them in the recycler view

        // first set the notifications back to 0
        NOTIFICATION_COUNT = 0;
        // then make a client request to get all the notifications
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());

        client = new AsyncHttpClient();
        client.get(RawrApp.DB_URL + "/notifications/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                notificationsAdapter.clear();

                Log.e(TAG, String.format("%s", response));
                try {
                    notificationsArray = response.getJSONArray("data");
                    populateNotifications(notificationsArray);
                    // swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    ((MainActivity) getActivity()).snackbarCallIndefinite("Error occurred while parsing json array for notifications");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg;
                Log.e(TAG, String.format("%s", errorResponse));
                try {
                    msg = errorResponse.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, String.format("%s", e));
                    msg = "Error (1) occurred in getNotifications.";
                }
                Log.e(TAG, msg);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ((MainActivity) getActivity()).snackbarCallLong(String.format("Error (3) in getNotifications: %s", responseString));
            }
        });
    }

    public void animateText() {
        // creates a fadeIn fadeOut animation with the text as it logs one in
        getView().findViewById(R.id.tv_notification_notice).setAlpha(0.0f);
        final AlphaAnimation a_go = new AlphaAnimation(0.0f, 1.0f);
        a_go.setDuration(1000);
        getView().findViewById(R.id.tv_notification_notice).startAnimation(a_go);
    }

    public void removeNotification(RawrNotification notification) {
        // removes a notification
        RequestParams params = new RequestParams();
        params.put("_id", notification.id);

        client = new AsyncHttpClient();
        client.post(RawrApp.DB_URL + "/notifications/delete_one", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.w(TAG, String.format("%s", response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
            }
        });
    }

    public void UpdateConversationsNotificationIndicator(boolean adding) {
        if (adding) {
            NOTIFICATION_COUNT++;
        } else {
            NOTIFICATION_COUNT--;
        }
        ((MainActivity) getActivity()).updateNotificationIndicator(2, NOTIFICATION_COUNT);
    }

    public void populateNotifications(JSONArray notificationObjectsArray) {
        // from this array of notification objects, populate the recycler view with the notification objects
        boolean newNotifications = false;
        for (int i = 0; i < notificationObjectsArray.length(); i++) {
            try {
                RawrNotification rn = RawrNotification.fromJSONServer(notificationObjectsArray.getJSONObject(i));
                if (!rn.sent) {
                    newNotifications = true;
                }
                mNotifications.add(rn);
                notificationsAdapter.notifyItemInserted(mNotifications.size() - 1);
                // set to true to add to the notification indicator counter by 1
                UpdateConversationsNotificationIndicator(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // tells the user to check his notifications channel by letting them know they have new notifications
        if (newNotifications) {
            ((MainActivity) getActivity()).snackbarCallLong("You have new notifications");
        }
        if (mNotifications.size() > 0) {
            // removes that view from being visiblie that says that the person has no notifications
            getView().findViewById(R.id.tv_notification_notice).setVisibility(View.GONE);
        } else {
            // set that view to visible
            getView().findViewById(R.id.tv_notification_notice).setVisibility(View.VISIBLE);
            animateText();
        }
    }
    public JSONArray getMessagesFromServer(){
        // returns all the messages from the current user
        // TODO - Implement this function, and call it in onCreateView
        return new JSONArray(); // CHANGE THIS TO THE ACTUAL RESPONSE
    }
}
