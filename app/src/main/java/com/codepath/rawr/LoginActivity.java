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

import com.codepath.rawr.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    // debugging
    public static final String TAG = "LoginActivity";
    // db
    AsyncHttpClient client;
    public String[] DB_URLS;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref; SharedPreferences.Editor spEditor; String user_id;
    // views
    ProgressBar pb;
    RelativeLayout parentView; // for snackbar
    EditText et_email;
    Button bt_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get server stuffs
        client = new AsyncHttpClient();
        DB_URLS = new String[] {getString(R.string.DB_HEROKU_URL), getString(R.string.DB_LOCAL_URL)};

        // get the views
        parentView = (RelativeLayout) findViewById(R.id.relativeLayoutInitLogin);
        pb = (ProgressBar) findViewById(R.id.progressBarLoginActivity);
        et_email = (EditText) findViewById(R.id.et_email);
        bt_login = (Button) findViewById(R.id.bt_login);

        // make progress visible because we're doing internet stuffs, disables submit button
        setProgressVisible();

        // check if the user is logged in with the SharedPreferences, first get shared pref
        sharedPref = this.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);
        checkIfUserLogged();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // login the user via the email in the edit text
                loginUser(et_email.getText().toString());
            }
        });
    }

    // make snackbars more quickly
    public void snackbarCall(String message) {
        Snackbar.make(parentView, String.format("%s", message), Snackbar.LENGTH_LONG).show();
    }

    public void checkIfUserLogged() {
        // get the id saved, if it's not saved, add, move to logoutActivitity to sign in or sign up
        user_id = sharedPref.getString(getString(R.string.sp_string_user_id_key), null);
        // if the user is empty, then we launch the logout activity because we know he's not logged in
        if (user_id == null) {
            setProgressDead();
        } else {
            // otherwise sign user in
            signInUser(user_id);
        }
    }

    public void setProgressVisible() {
        bt_login.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
    }
    public void setProgressDead() {
        bt_login.setEnabled(true);
        pb.setVisibility(View.GONE);
    }

    public void signInUser(String id) {
        // make a https request for getting the user with this id
        RequestParams params = new RequestParams();
        params.put("uid", id);
        client.get(DB_URLS[0] + "/user_get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    loginUser(usingUser.email);
                    setProgressDead();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Error while parsing JSON in sign in user: %s", response));
                    setProgressDead();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
                // remove progress bar because this means that the user is not signed in
                setProgressDead();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // remove progress bar because this means that the user is not signed in
                setProgressDead();
            }
        });
    }

    // make a login call to the database, this is practically the same as /user_get and is probably unnecessary... but lol.
    public void loginUser(String email) {
        setProgressVisible(); // make the progress bar visible
        RequestParams params = new RequestParams();
        params.put("email", email);
        client.get(DB_URLS[0] + "/login", params, new JsonHttpResponseHandler() {
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
            LoginActivity.this.finishAffinity(); // finish this activity so that we don't go back to it
        } catch (Exception e) {
            e.printStackTrace();
            snackbarCall("JSON exception occurred while logging you in at updateUsingUserAndLauchMainActivity. It's not your fault.");
        }
    }

    public void launchMainActivity() {
        // launch the main activity if all goes right
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }
}
