package com.codepath.rawr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class SenderFormActivity extends AppCompatActivity {

    public CheckBox cb_envelope, cb_smallBox, cb_largeBox, cb_clothing, cb_other, cb_liquid, cb_fragile;
    public boolean bool_envelope, bool_smallBox, bool_largeBox, bool_clothing, bool_other, bool_liquid, bool_fragile = false;
    public EditText et_dropoff, et_pickup, et_name, et_other, et_no, et_payment, et_details;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_form);

        cb_envelope = (CheckBox) findViewById(R.id.cb_envelope);
        cb_smallBox = (CheckBox) findViewById(R.id.cb_smallBox);
        cb_largeBox = (CheckBox) findViewById(R.id.cb_largeBox);
        cb_clothing = (CheckBox) findViewById(R.id.cb_clothing);
        cb_other = (CheckBox) findViewById(R.id.cb_other);
       // cb_liquid = (CheckBox) findViewById(R.id.cb_liquid);
      //  cb_fragile = (CheckBox) findViewById(R.id.cb_fragile);
        et_dropoff = (EditText) findViewById(R.id.et_dropoff);
        et_pickup = (EditText) findViewById(R.id.et_pickup);
        et_name = (EditText) findViewById(R.id.et_name);
        et_other = (EditText) findViewById(R.id.et_other);
        et_no = (EditText) findViewById(R.id.et_no);
        et_payment = (EditText) findViewById(R.id.et_payment);
        et_details = (EditText) findViewById(R.id.et_details);
    }


    public void onCheckboxClicked(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.cb_envelope: {
                bool_envelope = checked;
                break;
            }
            case R.id.cb_smallBox: {
                bool_smallBox = checked;
                break;
            }
            case R.id.cb_largeBox: {
                bool_largeBox = checked;
                break;
            }
            case R.id.cb_clothing: {
                bool_clothing = checked;
                break;
            }
            case R.id.cb_other: {
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
