package com.codepath.rawr.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/21/17.
 */

public class ShippingRequest {
    public String id, travelNoticeId, requesterId;
    public boolean sending, receiving, accepted, declined;
    public boolean item_envelopes, item_smbox, item_lgbox, item_clothing, item_fragile, item_liquid, item_other;

    public String drop_off_flexibility, pick_up_flexibility;

    public boolean isInvalid(){
        return this.accepted && this.declined;
    }
    public boolean isAccepted(){
        return this.accepted && !this.declined;
    }
    public boolean isPending(){
        return !this.accepted && !this.declined;
    }
    public boolean isDeclined(){
        return !this.accepted && this.declined;
    }

    public ShippingRequest fromJSONServer(JSONObject response) throws JSONException {
        ShippingRequest sr = new ShippingRequest();
        sr.id = response.getString("_id");
        sr.travelNoticeId = response.getString("_id");
        sr.requesterId = response.getString("_id");

        sr.sending = response.getBoolean("sending");
        sr.receiving = response.getBoolean("receiving");
        sr.accepted = response.getBoolean("accepted");
        sr.declined = response.getBoolean("declined");

        sr.item_envelopes = response.getBoolean("item_envelopes");
        sr.item_smbox = response.getBoolean("item_smbox");
        sr.item_lgbox = response.getBoolean("item_lgbox");
        sr.item_clothing = response.getBoolean("item_clothing");
        sr.item_fragile = response.getBoolean("item_fragile");
        sr.item_liquid = response.getBoolean("item_liquid");
        sr.item_other = response.getBoolean("item_other");

        // if the flexibilities are put, update flexibilities
        try {
            // we do a try catch since both these infor
            sr.drop_off_flexibility = response.getString("drop_off_flexibility");
        } catch (JSONException e) {
            sr.drop_off_flexibility = null;
        }
        try {
            sr.pick_up_flexibility = response.getString("pick_up_flexibility");
        } catch (JSONException e) {
            sr.pick_up_flexibility = null;
        }

        return sr;
    }
}
