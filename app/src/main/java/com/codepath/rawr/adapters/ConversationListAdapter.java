//package com.codepath.rawr.adapters;
//
//import android.content.Context;
//import android.media.Image;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.codepath.rawr.R;
//import com.codepath.rawr.models.ShippingRequest;
//
//import java.util.List;
//
//import static com.codepath.rawr.R.id.iv_profile;
//import static com.codepath.rawr.R.id.tv_from;
//
///**
// * Created by mandaleeyp on 7/28/17.
// */
//
//public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {
//
//    public List<Messages> mMessages;
//    Context context;
//
//    public ConversationListAdapter(List<Messages> messages) {
//        mMessages = messages;
//    }
//
//    @Override
//    public ConversationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View requestView = inflater.inflate(R.layout.item_conversation_list parent, false);
//        ViewHolder viewHolder = new ViewHolder(requestView) {
//        };
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(final ConversationListAdapter.ViewHolder holder, int position) {
//
//        Messages message = mMessages.get(position);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mMessages.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//
//        public ImageView iv_profile;
//        public TextView tv_user;
//        public TextView tv_date;
//        public TextView tv_message;
//
//        public ViewHolder(View itemView){
//            super(itemView);
//
//            iv_profile = (ImageView) itemView.findViewById(R.id.iv_profile);
//            tv_user = (TextView) itemView.findViewById(R.id.tv_user);
//            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
//            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
//
//        }
//
//        public void clear() {
//            mMessages.clear();
//            notifyDataSetChanged();
//        }
//
//        // Add a list of items -- change to type used
//        public void addAll(List<Messages> list) {
//            mMessages.addAll(list);
//            notifyDataSetChanged();
//        }
//    }
//
//}
