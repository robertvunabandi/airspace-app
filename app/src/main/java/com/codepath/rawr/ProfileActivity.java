package com.codepath.rawr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.rawr.models.RawrImages;
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

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    // views
    ImageView iv_profile_image;
    CoordinatorLayout parentLayout;
    TextView tv_trips_counter, tv_dollars_made_counter, tv_items_counter, tv_fullName, tv_location;
    // Setting up database
    AsyncHttpClient client;
    User usingUser;
    // Tag for debugging
    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = new AsyncHttpClient();

        parentLayout = (CoordinatorLayout) findViewById(R.id.profileParentLayout);

        // Initializing views
        iv_profile_image = (ImageView) findViewById(R.id.iv_profile_image);
        tv_fullName = (TextView) findViewById(R.id.tv_fullName);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_trips_counter = (TextView) findViewById(R.id.tv_trips_counter);
        tv_dollars_made_counter = (TextView) findViewById(R.id.tv_dollars_made_counter);
        tv_items_counter = (TextView) findViewById(R.id.tv_items_counter);

        // do other functionalities
        iv_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });

        // get the using user so we can do stuff with it
        getUsingUser();

        // load the profile image of the user that's currently there with Glide and Firebase
        StorageReference ref = RawrApp.getStorageReferenceForImageFromFirebase(RawrApp.getUsingUserId());
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(ref)
                .placeholder(R.drawable.ic_android)
                .error(R.drawable.ic_air_space_2)
                .into(iv_profile_image);

    }

    public void populateUsersData() {
        // change the personal details of the user
        tv_fullName.setText(usingUser.getFullName());
        tv_location.setText(usingUser.location);
        tv_trips_counter.setText(String.valueOf(usingUser.tripsTaken));
        tv_dollars_made_counter.setText(String.valueOf(usingUser.dollarsMade));
        tv_items_counter.setText(String.valueOf(usingUser.itemsSent));
    }

    public void getImageFromAlbum() {
        // starts an intent for
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RawrApp.CODE_LOAD_PROFILE_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            // for loading images, this ma
            try {
                // get the image from the cellphone
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream); // Bitmaps are the ones to be placed/replaced in imageViews
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
        String imageTitleDatabase = RawrApp.getUsingUserId()+".png";
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

    public void getUsingUser() {
        // make a call to server to get the user and then create usingUser base on that json from the server
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());
        client.get(RawrApp.DB_URL + "/user/get", params, new JsonHttpResponseHandler() {
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

}
