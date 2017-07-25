package com.codepath.rawr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ReceiverFormActivity extends AppCompatActivity {

    public CheckBox cb_envelope, cb_smallBox, cb_largeBox, cb_clothing, cb_other, cb_liquid, cb_fragile;
    public boolean item_envelope, item_smallBox, item_largeBox, item_clothing, item_other, item_liquid, item_fragile = false;
    public EditText et_dropoff, et_pickup, et_name, et_other, et_phone, et_email, et_payment, et_details;
    public Button bt_confirm;

    // for debugging and snackbar
    public final static String TAG = "S:ReceiverFormActivity";
    public ScrollView parentLayout;

    // to get the user and on activity result for where this comes from
    public User usingUser;
    public Intent resultIntent;
    public int action;

    // for server
    AsyncHttpClient client;
    public String[] DB_URLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_form);

        // get stuff needed for db
        DB_URLS = new String[]{getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};
        client = new AsyncHttpClient();
        action = getIntent().getIntExtra("action", 0);
        resultIntent = new Intent();

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
        et_phone = (EditText) findViewById(R.id.et_no);
        et_payment = (EditText) findViewById(R.id.et_payment);
        et_details = (EditText) findViewById(R.id.et_details);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);

        // get the parent layout
        parentLayout = (ScrollView) findViewById(R.id.scrollViewReceiverForm);

        // make the button deactivated until we get the user using the app
        bt_confirm.setEnabled(false);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        // get the user using the app, which activates the button
        getUsingUser();
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

    public int getItemTotal() {
        // this implies that the person has to choose at least one item!
        int res = 0;
        boolean[] itemBools = new boolean[]{cb_envelope.isChecked(), cb_smallBox.isChecked(), cb_largeBox.isChecked(), cb_clothing.isChecked(), cb_fragile.isChecked(), cb_liquid.isChecked(), cb_other.isChecked()};
        for (int i = 0; i < itemBools.length; i++) {
            if (itemBools[i]) res += 1;
        }
        return res;
    }

    public RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", getIntent().getExtras().getString("travel_notice_id"));
        params.put("ruid", RawrApp.getUsingUserId());
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
            params.put("recipient_phone", et_phone.getText().toString());
            params.put("recipient_email", et_email.getText().toString());
            params.put("recipient_uses_app", false);

            params.put("deliverer_name", usingUser.getFullName());
            params.put("deliverer_email", usingUser.email);
            params.put("deliverer_phone", usingUser.phone);
            params.put("deliverer_uses_app", true);
        } else if (action == 1) {
            // meaning receiving
            params.put("deliverer_name", et_name.getText().toString());
            params.put("deliverer_phone", et_phone.getText().toString());
            params.put("deliverer_email", et_email.getText().toString());
            params.put("deliverer_uses_app", false);

            params.put("recipient_name", usingUser.getFullName());
            params.put("recipient_email", usingUser.email);
            params.put("recipient_phone", usingUser.phone);
            params.put("recipient_uses_app", true);
        }

        params.put("item_total", getItemTotal()); // TODO - CHANGE ITEM TOTAL TO SOMETHING
        return params;
    }

    public boolean isRecipientFilled() {
        // checks if these information are empty or not with the .isEmpty() string method and returns the opposite of that (logic here haha)
        boolean b1 = !(et_name.getText().toString().equals(null) || et_phone.getText().toString().equals(null) || et_email.getText().toString().equals(null));
        return b1 && !(et_name.getText().toString().isEmpty() || et_phone.getText().toString().isEmpty() || et_email.getText().toString().isEmpty());
    }

    public void sendRequest() {
        RequestParams params = getParams();

        if (getItemTotal() == 0) {
            // don't send request and tell user that he has to pick at least one checkbox
            Log.e(TAG, "item total is 0");
            Snackbar.make(parentLayout, "You must select at least one checkbox.", Snackbar.LENGTH_LONG).show();
        } else if (!isRecipientFilled()) {
            Log.e(TAG, "Recipient info not filled up");
            // checks if the recipient's informations is filled up because the server will also throw an error if it's not
            Snackbar.make(parentLayout, "You must fill up all of the recipient's details.", Snackbar.LENGTH_LONG).show();
        } else {
            client.post(DB_URLS[0] + "/request_send", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // if request sent successfully, set the result ok
                    // no need for try/catch since we don't do anything with the JSON anyway
                    resultIntent.putExtra("message", "success");
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                // TODO - One of the onFailure may say that the request has already been sent. We have to put that in the resultIntent as message
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                    // if an error occurred, set result cancelled
                    try {
                        resultIntent.putExtra("message", errorResponse.getString("message"));
                        setResult(RESULT_CANCELED, resultIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        resultIntent.putExtra("message", "Error (1) in endpoint request_send");
                        setResult(RESULT_CANCELED, resultIntent);
                        finish();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                    // if an error occurred, set result cancelled
                    resultIntent.putExtra("message", "Error in (2) endpoint request_send");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, String.format("%s", responseString));
                    // if an error occurred, set result cancelled
                    resultIntent.putExtra("message", "Error in (3) endpoint request_send");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }
            });
        }
    }

    public void getUsingUser() {
        // make a call to server to get the user and then create usingUser base on that json from the server
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());
        client.get(DB_URLS[0] + "/user_get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // populate the usingUser from the JSON received here, then enable the bt_confirm
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    bt_confirm.setEnabled(true);
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Parsing JSON excepted %s", e));
                    Toast.makeText(getBaseContext(), String.format("User is not gotten, JSON parsing error: %s", e), Toast.LENGTH_LONG).show();
                    // if an error occurred, set result cancelled because we need the user!
                    resultIntent.putExtra("message", "Error in parsing user JSON from the server");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                // TODO - Remove Toast
                Toast.makeText(getBaseContext(), String.format("User not gotten error 1 %s", errorResponse), Toast.LENGTH_LONG).show();
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                // TODO - Remove Toast
                Toast.makeText(getBaseContext(), String.format("User not gotten error 2 %s", errorResponse), Toast.LENGTH_LONG).show();
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // TODO - Remove Toast
                Toast.makeText(getBaseContext(), String.format("User not gotten error 3 %s", responseString), Toast.LENGTH_LONG).show();
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }
}
