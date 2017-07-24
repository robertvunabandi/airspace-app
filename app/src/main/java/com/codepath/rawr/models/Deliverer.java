package com.codepath.rawr.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by robertvunabandi on 7/24/17.
 */

public class Deliverer {
    public String name, email, phone;
    public boolean uses_app;

    public static Deliverer fromJSONServer(JSONObject jsonObject) throws JSONException {
        Deliverer dlr = new Deliverer();
        dlr.name = jsonObject.getString("name");
        dlr.email = jsonObject.getString("email");
        dlr.phone = jsonObject.getString("phone");
        dlr.uses_app = jsonObject.getBoolean("uses_app");
        return dlr;
    }

    // if uses_app, the information about the person can be retrieved by making a call to the server with endpoint /user_get and parameter email = <email>
}
