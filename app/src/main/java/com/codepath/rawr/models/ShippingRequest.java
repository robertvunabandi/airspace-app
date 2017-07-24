package com.codepath.rawr.models;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/21/17.
 */

public class ShippingRequest {
    public String id, travelNoticeId, requesterId;
    public Recipient recipient;
    public Deliverer deliverer;
    public int action, status, item_total;
    public boolean item_envelopes, item_smbox, item_lgbox, item_clothing, item_fragile, item_liquid, item_other;

    /**
     * HERE
     * DROP_OFF_FLEX MEANS: FLEXIBILITY OF THE REQUESTER TO GET THE ITEM DROPPED OFF TO TRAVELER WHEN TRAVELER PICKS UP THE ITEM TO BE SHIPPED
     * PICK_UP_FLEX MEANS: FLEXIBILITY OF THE REQUESTER TO GET THE ITEM PICKED UP FROM TRAVELER WHEN TRAVELER DROPS OFF THE ITEM AFTER TRAVELLING
     * */
    public String item_other_name, drop_off_flexibility, pick_up_flexibility;

    /* action codes::= 0: sending, 1: receiving */
    public boolean isSending(){
        return this.action == 0;
    }
    public boolean isReceiving(){
        return this.action == 1;
    }
    /* status codes::= 0: pending, 1: accepted, 2: declined, 3: invalid */
    public boolean isInvalid(){
        return this.status == 3;
    }
    public boolean isAccepted(){
        return this.status == 1;
    }
    public boolean isPending(){
        return this.status == 0;
    }
    public boolean isDeclined(){
        return this.status == 2;
    }

    public ShippingRequest fromJSONServer(JSONObject response) throws JSONException {
        ShippingRequest sr = new ShippingRequest();
        sr.id = response.getString("_id");
        sr.travelNoticeId = response.getString("travel_notice_id");
        sr.requesterId = response.getString("ruid");
        sr.action = response.getInt("action");
        sr.status = response.getInt("status");

        sr.item_envelopes = response.getBoolean("item_envelopes");
        sr.item_smbox = response.getBoolean("item_smbox");
        sr.item_lgbox = response.getBoolean("item_lgbox");
        sr.item_clothing = response.getBoolean("item_clothing");
        sr.item_fragile = response.getBoolean("item_fragile");
        sr.item_liquid = response.getBoolean("item_liquid");
        sr.item_other = response.getBoolean("item_other");
        if (sr.item_other) {
            sr.item_other_name = response.getString("item_other_name");
        } else {
            sr.item_other_name = null;
        }
        sr.item_total = response.getInt("item_total");

        sr.recipient = Recipient.fromJSONServer(response.getJSONObject("recipient"));
        sr.deliverer = Deliverer.fromJSONServer(response.getJSONObject("deliverer"));

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


    /* Creates parameters for sending a request to our database directly
    from this travel notice using its own data, which is why the "this". */
    public RequestParams createParams() {
        /* creates parameter for endpoint call /travel_notice_add and /travel_notice_update */
        // TODO - be careful about _id being null
        RequestParams params = new RequestParams();
        // required, 4 parameters
        params.put("_id", this.id);
        params.put("travel_notice_id", this.travelNoticeId);
        params.put("ruid", this.requesterId);
        params.put("action", this.action);
        params.put("status", this.status);
        // optional: 8 parameters
        params.put("item_envelopes", this.item_envelopes);
        params.put("item_smbox", this.item_smbox);
        params.put("item_lgbox", this.item_lgbox);
        params.put("item_clothing", this.item_clothing);
        params.put("item_fragile", this.item_fragile);
        params.put("item_liquid", this.item_liquid);
        params.put("item_other", this.item_other);
        params.put("item_other_name", this.item_other_name);
        // required, 1 parameter
        params.put("item_total", this.item_total);
        //  deliverer
        params.put("deliverer_name", this.deliverer.name);
        params.put("deliverer_email", this.deliverer.email);
        params.put("deliverer_phone", this.deliverer.phone);
        params.put("deliverer_uses_app", this.deliverer.uses_app);
        // recipient
        params.put("recipient_name", this.recipient.name);
        params.put("recipient_email", this.recipient.email);
        params.put("recipient_phone", this.recipient.phone);
        params.put("recipient_uses_app", this.recipient.uses_app);
        // optional: 2 flexibilities
        params.put("drop_off_flexibility", this.drop_off_flexibility);
        params.put("pick_up_flexibility", this.pick_up_flexibility);
        return params;
    }
}
