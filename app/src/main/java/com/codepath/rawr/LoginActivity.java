package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

public class LoginActivity extends AppCompatActivity {
    // debugging
    public static final String TAG = "LoginActivity";
    // db
    AsyncHttpClient client;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref; SharedPreferences.Editor spEditor; String user_id;
    // views
    ProgressBar pb;
    RelativeLayout parentView; // for snackbar
    TextView loginYouIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get server stuffs
        client = new AsyncHttpClient();

        // get the views
        parentView = (RelativeLayout) findViewById(R.id.relativeLayoutInitLogin);
        pb = (ProgressBar) findViewById(R.id.progressBarLoginActivity);
        loginYouIn = (TextView) findViewById(R.id.tv_loginYouIn);

        // make progress visible because we're doing internet stuffs, disables submit button
        setProgressVisible();
        animateText();

        // check if the user is logged in with the SharedPreferences, first get shared pref
        sharedPref = this.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);
        checkIfUserLogged();
    }

    public void animateText() {
        // creates a fadeIn fadeOut animation with the text as it logs one in
        final AlphaAnimation a_go = new AlphaAnimation(0.1f, 1.0f);
        final AlphaAnimation a_back = new AlphaAnimation(1.0f, 0.1f);
        a_go.setDuration(1500); a_back.setDuration(1500);
        a_go.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                loginYouIn.startAnimation(a_back);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        a_back.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                loginYouIn.startAnimation(a_go);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loginYouIn.startAnimation(a_go);
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
            // the only way to get here is from logoutActivity, so it will send us back there
            finish();
        } else {
            // otherwise sign user in
            signInUser(user_id);
        }
    }

    public void setProgressVisible() {
        pb.setVisibility(View.VISIBLE);
    }
    public void setProgressDead() {
        pb.setVisibility(View.GONE);
    }

    public void signInUser(String id) {
        // make a https request for getting the user with this id
        RequestParams params = new RequestParams();
        params.put("uid", id);
        client.get(RawrApp.DB_URL + "/user/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    loginUser(usingUser.email);
                    setProgressDead();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Error while parsing JSON in sign in user: %s", response));
                    setProgressDead();
                    // send back to logout
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("%s", errorResponse));
                // remove progress bar because this means that the user is not signed in
                setProgressDead();
                // send back to logout
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                // remove progress bar because this means that the user is not signed in
                setProgressDead();
                // send back to logout
                finish();
            }
        });
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
                    // launch main activity, which confirms that the user is logged in
                    launchMainActivity();
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

    public void launchMainActivity() {
        setProgressDead();
        // launch the main activity if all goes right
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
        LoginActivity.this.finishAffinity();
    }
}
