package com.codepath.rawr.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/24/17.
 */

public class Recipient {
    public String name, email, phone;
    public boolean uses_app;

    public static Recipient fromJSONServer(JSONObject jsonObject) throws JSONException {
        Recipient rcp = new Recipient();
        rcp.name = jsonObject.getString("name");
        rcp.email = jsonObject.getString("email");
        rcp.phone = jsonObject.getString("phone");
        rcp.uses_app = jsonObject.getBoolean("uses_app");
        return rcp;
    }
    // if uses_app, the information about the person can be retrieved by making a call to the server with endpoint /user_get and parameter email = <email>
}
