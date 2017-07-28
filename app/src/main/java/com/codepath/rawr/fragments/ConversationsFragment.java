package com.codepath.rawr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.rawr.R;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;

public class ConversationsFragment extends Fragment {
    // variables for HTTP calls
    public String[] DB_URLS;
    AsyncHttpClient client;

    // Declaring variables for messages
//    ConversationListAdapter conversationListAdapter;
//    ArrayList(Messages) mMessages;
//    RecyclerView rv_convos;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new AsyncHttpClient();
        DB_URLS = new String[] {getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);

        // get the variables for all the texts. Needed: Name, time,

        // populate the recycler view of messages with messages from the server

//        mMessages = new ArrayList<>();
//        conversationListAdapter = new ConversationListAdapter(mMessages);
//        rv_convos = (RecyclerView) v.findViewById(rv_convos);
//        rv_convos.setLayoutManager(new LinearLayoutManager(getContext()));
//        rv_convos.setAdapter(conversationListAdapter);


        return v;
    }

    public JSONArray getMessagesFromServer(){
        // returns all the messages from the current user
        // TODO - Implement this function, and call it in onCreateView
        return new JSONArray(); // CHANGE THIS TO THE ACTUAL RESPONSE
    }
}
