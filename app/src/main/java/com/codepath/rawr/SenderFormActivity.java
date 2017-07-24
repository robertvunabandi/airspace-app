package com.codepath.rawr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.codepath.rawr.models.TravelNotice;
import com.loopj.android.http.AsyncHttpClient;

public class SenderFormActivity extends AppCompatActivity {

    public CheckBox cb_envelope, cb_smallBox, cb_largeBox, cb_clothing, cb_other, cb_liquid, cb_fragile;
    public boolean item_envelope, item_smallBox, item_largeBox, item_clothing, item_other, item_liquid, item_fragile = false;
    public EditText et_dropoff, et_pickup, et_name, et_other, et_no, et_payment, et_details;
    public Button bt_confirm;
    public TravelNotice tvl;

    public final static String TAG = "S:SenderFormActivity";


    AsyncHttpClient client;
    public String[] DB_URLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_form);

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
        et_no = (EditText) findViewById(R.id.et_no);
        et_payment = (EditText) findViewById(R.id.et_payment);
        et_details = (EditText) findViewById(R.id.et_details);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

}
