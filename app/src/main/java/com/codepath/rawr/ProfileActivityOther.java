package com.codepath.rawr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.rawr.models.RawrImages;
import com.codepath.rawr.models.SuitcaseColor;
import com.codepath.rawr.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProfileActivityOther extends AppCompatActivity {
    private String userId;

    // views
    ImageView iv_profile_image, iv_profile_activity_banner;
    CoordinatorLayout parentLayout;
    TextView tv_trips_counter, tv_dollars_made_counter, tv_items_counter, tv_fullName, tv_location, tv_wished_location;
    RelativeLayout rl_profile_activity_banner, im_suitcase_color_on_detail;
    Button bt_edit_profile;
    // Setting up database
    AsyncHttpClient client;
    User usingUser;
    // Tag for debugging
    private static final String TAG = "ProfileActivityOther";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = new AsyncHttpClient();

        userId = getIntent().getExtras().getString("user_id");

        parentLayout = (CoordinatorLayout) findViewById(R.id.profileParentLayout);

        // Initializing views
        iv_profile_image = (ImageView) findViewById(R.id.iv_profile_image);
        iv_profile_activity_banner = (ImageView) findViewById(R.id.iv_profile_activity_banner);
        tv_fullName = (TextView) findViewById(R.id.tv_fullName);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_trips_counter = (TextView) findViewById(R.id.tv_trips_counter);
        tv_dollars_made_counter = (TextView) findViewById(R.id.tv_dollars_made_counter);
        tv_items_counter = (TextView) findViewById(R.id.tv_items_counter);
        tv_wished_location = (TextView) findViewById(R.id.tv_wished_location);
        bt_edit_profile = (Button) findViewById(R.id.bt_edit_profile);
        rl_profile_activity_banner = (RelativeLayout) findViewById(R.id.rl_profile_activity_banner);
        im_suitcase_color_on_detail = (RelativeLayout) findViewById(R.id.im_suitcase_color_on_detail);



        // get the using user so we can do stuff with it
        getUsingUser(userId);

        // load the profile image of the user that's currently there with Glide and Firebase
        StorageReference ref = RawrApp.getStorageReferenceForImageFromFirebase(userId);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(ref)
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_air_space_2)
                .into(iv_profile_image);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void populateUsersData() {
        // change the personal details of the user
        tv_fullName.setText(usingUser.getFullName());
        tv_location.setText(usingUser.location);
        tv_trips_counter.setText(String.valueOf(usingUser.tripsTaken));
        tv_dollars_made_counter.setText(String.valueOf(usingUser.dollarsMade));
        tv_items_counter.setText(String.valueOf(usingUser.itemsSent));
        tv_wished_location.setText(usingUser.fName + " likes to go to "+ usingUser.favoriteTravelPlace);

        // update the banner to the user's suitcase color
        if (!usingUser.suitcaseColor.isRainbow()) {
            // set the color of the suitcase
            im_suitcase_color_on_detail.setBackgroundColor(getColor(usingUser.suitcaseColor.getDrawableId()));
            // change the color of the banner
            iv_profile_activity_banner.setVisibility(View.INVISIBLE);
            // TODO - Make sure this part works... We need to add edit profile activity to check that
            rl_profile_activity_banner.setBackgroundTintList(ColorStateList.valueOf(getColor(usingUser.suitcaseColor.getDrawableId())));

        } else {
            // set the color of the suitcase to something random
            // rsci stands for random suitcase color integer, we want to be above index 1 and below index 8, and on top of that, get rid of 4 undesired colors
            int rsci = (int) Math.round(Math.random()*(SuitcaseColor.getStringLength() - 1 - 4)) + 2;
            SuitcaseColor randomSuitcaseColor = new SuitcaseColor(rsci);
            im_suitcase_color_on_detail.setBackgroundColor(getColor(randomSuitcaseColor.getDrawableId()));
            // change the color of the banner
            iv_profile_activity_banner.setImageDrawable(getDrawable(usingUser.suitcaseColor.getDrawableId()));
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            // for loading images, this ma
            try {
                // get the image from the cellphone
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream); // Bitmaps are the ones to be placed/replaced in imageViews
                checkAndRequestPermissions();
                // save the image to Firebase
                saveProfileImageToFirebase(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("Bitmap error! %s", e));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            Snackbar.make(parentLayout, "Cancelled loading profile image", Snackbar.LENGTH_LONG).show();
        }

    }

    public void saveProfileImageToFirebase(final Bitmap image) {
        // create the string of the image, which is based on this person's id
        String imageTitleDatabase = RawrApp.getUsingUserId() + ".png";
        // convert the image first to byte array
        byte[] imageByte = RawrImages.convertImageToByteArray(image);
        // store image to firebase storage by first getting the reference to that image based on the user id
        final StorageReference ref = FirebaseStorage.getInstance().getReference(imageTitleDatabase);
        ref.putBytes(imageByte).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // set the profile image to this image
                    iv_profile_image.setImageBitmap(image);

                } else {
                    snackbarCallIndefinite("An error occurred while uploading your image. Maximum image size may have been exceeded.");
                }
            }

        });
    }

    public void getUsingUser(String id) {
        // make a call to server to get the user and then create usingUser base on that json from the server
        RequestParams params = new RequestParams();
        params.put("uid", id);
        client.get(RawrApp.DB_URL + "/user/get", params, new JsonHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // populate the usingUser from the JSON received here, then enable the bt_confirm
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    populateUsersData();
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Parsing JSON excepted %s", e));
                    // quit this activity because this error will cause more errors
                    snackbarCallIndefinite("JSON error in parsing user object");
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                snackbarCallIndefinite(String.format("User not found %s", errorResponse));
                // quit this activity because this error will cause more errors
                finish();
            }

        });
    }

    // Snackbar calls
    public void snackbarCall(String message, int length) {
        Snackbar.make(parentLayout, String.format("%s", message), length).show();
    }

    public void snackbarCallIndefinite(String message) {
        snackbarCall(message, Snackbar.LENGTH_INDEFINITE);
    }

    public void snackbarCallLong(String message) {
        snackbarCall(message, Snackbar.LENGTH_LONG);
    }

    public void snackbarCallShort(String message) {
        snackbarCall(message, Snackbar.LENGTH_SHORT);
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ext_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ext_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
