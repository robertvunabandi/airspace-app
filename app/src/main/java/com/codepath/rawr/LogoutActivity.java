package com.codepath.rawr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LogoutActivity extends AppCompatActivity {
    // views
    public Button bt_login, bt_signup;
    public TextView tv_appName;
    public RelativeLayout parentLayout;

    // variables
    public String messageFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        // get the views
        parentLayout = (RelativeLayout) findViewById(R.id.ActivityLogoutInitRelativeLayout);
        tv_appName = (TextView) findViewById(R.id.tv_appName);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);

        // get intent in case this is coming from another activity, and launch a snackbar
        messageFromIntent = getIntent().getExtras().getString("message", null);
        if (!(messageFromIntent == null || messageFromIntent.isEmpty())) {
            // if the message is not empty, do a snackbar
            Snackbar.make(parentLayout, String.format("%s", messageFromIntent), Snackbar.LENGTH_LONG).show();
        }

        // set login click listener
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity of login
                Intent loginActivity = new Intent(LogoutActivity.this, LoginActivity.class);
                startActivity(loginActivity);
            }
        });

        // set signup click listener
        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity of signup
                Intent signupActivity = new Intent(LogoutActivity.this, SignupActivity.class);
                startActivity(signupActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
