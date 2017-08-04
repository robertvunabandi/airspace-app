package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LogoutActivity extends AppCompatActivity {
    // debugging
    public static final String TAG = "LogoutActivity";
    // db
    AsyncHttpClient client;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref; SharedPreferences.Editor spEditor; String user_id;
    // views
    ProgressBar pb;
    RelativeLayout parentView; // for snackbar
    public Button bt_login, bt_signup;
    public TextView tv_appName;
    public EditText et_email;

    // variables
    public String messageFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        // initialize the client and activate shared pref
        client = new AsyncHttpClient();
        sharedPref = this.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);
        // get the views
        parentView = (RelativeLayout) findViewById(R.id.ActivityLogoutInitRelativeLayout);
        pb = (ProgressBar) findViewById(R.id.progressBarLogoutActivity);
        tv_appName = (TextView) findViewById(R.id.tv_appName);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        et_email = (EditText) findViewById(R.id.tv_tvlr_email);

        // get intent in case this is coming from another activity, and launch a snackbar
        messageFromIntent = getIntent().getExtras().getString("message", null);
        if (!(messageFromIntent == null || messageFromIntent.isEmpty())) {
            // if the message is not empty, do a snackbar
            snackbarCall(messageFromIntent);
        }

        // set login click listener
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // login the user via the email in the edit text
                loginUser(et_email.getText().toString());
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

    // make snackbars more quickly
    public void snackbarCall(String message) {
        Snackbar.make(parentView, String.format("%s", message), Snackbar.LENGTH_LONG).show();
    }

    public void setProgressVisible() {
        bt_login.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
    }
    public void setProgressDead() {
        bt_login.setEnabled(true);
        pb.setVisibility(View.GONE);
    }

    // make a login call to the database, this is practically the same as /user_get and is probably unnecessary... but lol.
    public void loginUser(String email) {
        setProgressVisible(); // make the progress bar visible
        RequestParams params = new RequestParams();
        params.put("email", email);
        client.get(RawrApp.DB_URL + "/user/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    updateUsingUserAndLauchMainActivity();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Error while parsing JSON in sign in user: %s", response));
                    setProgressDead();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
                // remove the progress bar because this means there is an error, snackbar the error
                setProgressDead();
                snackbarCall("It seems as though your credentials are incorrect. Please try again.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // remove the progress bar because this means there is an error, snackbar the error
                setProgressDead();
                snackbarCall("It seems as though your credentials are incorrect. Please try again.");
            }
        });
    }

    public void updateUsingUserAndLauchMainActivity(){
        try {
            // create the editor for shared preferences (this will be done in login or sign up activity, currently just for testing)
            spEditor = sharedPref.edit();
            spEditor.putString(getString(R.string.sp_string_user_id_key), usingUser.id);
            spEditor.commit();
            user_id = sharedPref.getString(getString(R.string.sp_string_user_id_key), null);
            Log.e(TAG, String.format("THIS: %s", user_id));
            RawrApp.setUsingUserId(usingUser.id);
            // launch main activity
            setProgressDead();
            launchMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
            snackbarCall("JSON exception occurred while logging you in at updateUsingUserAndLauchMainActivity. It's not your fault.");
        }
    }

    public void launchMainActivity() {
        // launch the main activity if all goes right
        Intent mainActivity = new Intent(LogoutActivity.this, LoginActivity.class);
        startActivity(mainActivity);
    }
}
