package com.codepath.rawr.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/28/17.
 */

public class RawrNotification {
    // required
    public String id, message;
    public RawrDate date;
    public int action;
    public boolean sent;
    // optionals
    private String tvl_from_id, rq_from_id, msg_from_id, usr_from_id;
    public boolean tvl_bool, rq_bool, msg_bool, usr_bool;
    /** Different actions allow us to do different things To use this, just check if the action
     * of the notification is equal to any of those integer codes.
     *
     * Each action tells which optional parameters have been supplied. See comment at end of
     * actions. TBD means "to be decided" and that it's not implemented in server yet. X
     * means none. */
    public static final int ACTION_REQUEST_RECEIVED = 10; // tvl_from_id, rq_from_id, usr_from_id
    public static final int ACTION_REQUEST_ACCEPTED = 11; // tvl_from_id, rq_from_id, usr_from_id
    public static final int ACTION_REQUEST_DECLINED = 12; // tvl_from_id, rq_from_id, usr_from_id
    public static final int ACTION_REQUEST_DELETED = 13; // tvl_from_id, rq_from_id, usr_from_id
    public static final int ACTION_MESSAGE_RECEIVED = 20; // TBD
    public static final int ACTION_TRAVEL_NOTICE_DELETED = 30; // X
    public static final int ACTION_TRAVEL_NOTICE_APPROACHING = 31; // TBD
    public static final int ACTION_TRAVEL_NOTICE_DELETED_REMOVED_REQUEST = 32; // usr_from_id
    public static final int ACTION_REQUEST_DEBUGGING = 999; // X


    public static RawrNotification fromJSONServer(JSONObject jsonObject) throws JSONException {
        RawrNotification rn = new RawrNotification();
        // required params
        rn.id = jsonObject.getString("_id");
        rn.message = jsonObject.getString("message");
        rn.sent = jsonObject.getBoolean("sent");
        rn.date = RawrDate.fromJSONServer(jsonObject.getJSONObject("date_received"));
        rn.action = jsonObject.getInt("action");

        // optional params
        try {
            rn.setTvl_from_id(jsonObject.getString("travel_notice_from_id"));
        } catch (JSONException e) {
            rn.tvl_bool = false;
        }

        try {
            rn.setRq_from_id(jsonObject.getString("request_from_id"));
        } catch (JSONException e) {
            rn.rq_bool = false;
        }

        try {
            rn.setMsg_from_id(jsonObject.getString("message_from_id"));
        } catch (JSONException e) {
            rn.msg_bool = false;
        }

        try {
            rn.setUsr_from_id(jsonObject.getString("user_from_id"));
        } catch (JSONException e) {
            rn.usr_bool = false;
        }
        return rn;
    }

    // private setters for private methods, you can't set these parameters from outside
    private void setTvl_from_id(String tvl_from_id) {
        this.tvl_from_id = tvl_from_id;
    }

    private void setRq_from_id(String rq_from_id) {
        this.rq_from_id = rq_from_id;
    }

    private void setMsg_from_id(String msg_from_id) {
        this.msg_from_id = msg_from_id;
    }

    private void setUsr_from_id(String usr_from_id) {
        this.usr_from_id = usr_from_id;
    }

    // getters for public getting private variables that cannot be modified from outside
    public String getTvl_from_id() {
        if (this.tvl_bool) {
            return tvl_from_id;
        } else {
            return null;
        }
    }

    public String getRq_from_id() {
        if (this.rq_bool) {
            return rq_from_id;
        } else {
            return null;
        }
    }

    public String getMsg_from_id() {
        if (this.msg_bool) {
            return msg_from_id;
        } else {
            return null;
        }
    }

    public String getUsr_from_id() {
        if (this.usr_bool) {
            return usr_from_id;
        } else {
            return null;
        }
    }
}
