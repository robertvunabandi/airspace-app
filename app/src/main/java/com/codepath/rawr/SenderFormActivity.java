package com.codepath.rawr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.rawr.models.TravelNotice;
import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SenderFormActivity extends AppCompatActivity {

    public CheckBox cb_envelope, cb_smallBox, cb_largeBox, cb_clothing, cb_other, cb_liquid, cb_fragile;
    public boolean item_envelope, item_smallBox, item_largeBox, item_clothing, item_other, item_liquid, item_fragile = false;
    public EditText et_dropoff, et_pickup, et_name, et_other, et_no, et_email, et_payment, et_details;
    public Button bt_confirm;
    public int action;
    public TravelNotice tvl;

    public final static String TAG = "S:SenderFormActivity";

    public User usingUser;

    AsyncHttpClient client;
    public String[] DB_URLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_form);

        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        client = new AsyncHttpClient();
        action = getIntent().getIntExtra("action", 0);

        cb_envelope = (CheckBox) findViewById(R.id.cb_envelope);
        cb_smallBox = (CheckBox) findViewById(R.id.cb_smallBox);
        cb_largeBox = (CheckBox) findViewById(R.id.cb_largeBox);
        cb_clothing = (CheckBox) findViewById(R.id.cb_clothing);
        cb_other = (CheckBox) findViewById(R.id.cb_other);
        cb_liquid = (CheckBox) findViewById(R.id.cb_liquids);
        cb_fragile = (CheckBox) findViewById(R.id.cb_fragile);
        et_dropoff = (EditText) findViewById(R.id.et_dropoff);
        et_pickup = (EditText) findViewById(R.id.et_pickup);
        et_name = (EditText) findViewById(R.id.et_name);
        et_other = (EditText) findViewById(R.id.et_other);
        et_email = (EditText) findViewById(R.id.et_email);
        et_no = (EditText) findViewById(R.id.et_no);
        et_payment = (EditText) findViewById(R.id.et_payment);
        et_details = (EditText) findViewById(R.id.et_details);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest();

            }
        });
    }


    public void onCheckboxClicked(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.cb_envelope: {
                item_envelope = checked;
                break;
            }
            case R.id.cb_smallBox: {
                item_smallBox = checked;
                break;
            }
            case R.id.cb_largeBox: {
                item_largeBox = checked;
                break;
            }
            case R.id.cb_clothing: {
                item_clothing = checked;
                break;
            }
            case R.id.cb_fragile: {
                item_fragile = checked;
                break;
            }
            case R.id.cb_liquids: {
                item_liquid = checked;
                break;
            }
            case R.id.cb_other: {
                item_other = checked;
                if (checked) {
                    et_other.setVisibility(View.VISIBLE);
                } else {
                    // make it invisible and clear the text in it
                    et_other.setVisibility(View.INVISIBLE);
                    et_other.setText("");
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    public RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", getIntent().getExtras().getString("travel_notice_id"));
        params.put("ruid", getIntent().getExtras().getString("ruid"));

        params.put("action", action);
        params.put("item_envelopes", cb_envelope.isChecked());
        params.put("item_smbox", cb_smallBox.isChecked());
        params.put("item_lgbox", cb_largeBox.isChecked());
        params.put("item_clothing", cb_clothing.isChecked());
        params.put("item_fragile", cb_fragile.isChecked());
        params.put("item_liquid", cb_liquid.isChecked());
        params.put("item_other", cb_other.isChecked());
        params.put("item_other_name", et_other.getText().toString());
        params.put("drop_off_flexibility", et_dropoff.getText().toString());
        params.put("pick_up_flexibility", et_pickup.getText().toString());

        if (action == 0) {
            // meaning sending
            params.put("recipient_name", et_name.getText().toString());
            params.put("recipient_phone", et_no.getText().toString());
            params.put("recipient_email", et_email.getText().toString());
            params.put("recipient_uses_app", false);

            params.put("deliverer_name", "rub");
            params.put("deliverer_email", "dumb@dumb.dumb");
            params.put("deliverer_phone", "210");
            params.put("deliverer_uses_app", true);
        } else if (action == 1){
            // meaning receiving
            params.put("deliverer_name", et_name.getText().toString());
            params.put("deliverer_phone", et_no.getText().toString());
            params.put("deliverer_email", et_email.getText().toString());
            params.put("deliverer_uses_app", false);

            params.put("recipient_name", "rub");
            params.put("recipient_email", "dumb@dumb.dumb");
            params.put("recipient_phone", "210");
            params.put("recipient_uses_app", true);
        }



        params.put("item_total", 1); // TODO - CHANGE ITEM TOTAL TO SOMETHING
        return params;
    }

    public void sendRequest() {

        RequestParams params = getParams();

        client.post(DB_URLS[0] + "/request_send", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    Toast.makeText(getBaseContext(), String.format("%s", response), Toast.LENGTH_LONG).show();
                    Toast.makeText(getBaseContext(), String.format("%s", response.getBoolean("success")), Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    String toastMessage = "JSON Parsing error in onSuccess";
                    Log.e("TravelFragment", String.format("%s, %s", e, toastMessage));
                    Toast.makeText(getBaseContext(), String.format("%s", toastMessage), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 1 %s", errorResponse), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                Toast.makeText(getBaseContext(), String.format("error 2 %s", errorResponse), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                Toast.makeText(getBaseContext(), String.format("error 3 %s", responseString), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void getUsingUser() {
        // TODO - Implement function to get the using user object
        // edit usingUser
    }
}
