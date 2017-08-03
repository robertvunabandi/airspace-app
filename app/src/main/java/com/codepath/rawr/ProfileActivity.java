package com.codepath.rawr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codepath.rawr.models.RawrImages;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    // views
    ImageView iv_profile_image;
    RelativeLayout parentLayout;

    // Setting up database
    public FirebaseAuth mAuth;
    AsyncHttpClient client;


    // Tag for debugging
    private static final String TAG = "ProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        // Initializing views
        iv_profile_image = (ImageView) findViewById(R.id.iv_profile_image);

        iv_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });
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
                // convert image to bytes
                byte[] imageByte = RawrImages.convertImageToByteArray(selectedImage);
                iv_profile_image.setImageBitmap(selectedImage);
//                saveProfileImageToFirebase(selectedImage);
                // convert image back to bitmap
                // this replaces the image
                // ConversationsFragment convoFragment = (ConversationsFragment) pagerAdapter.getItem(vpPager.getCurrentItem());
                // ((ImageView) convoFragment.getView().findViewById(R.id.temporary_addProfileImageButton)).setImageBitmap(testImg);
                // once we get the image, we send the image with the enpoint

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("Bitmap error! %s", e));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            Snackbar.make(parentLayout, "Cancelled loading profile image", Snackbar.LENGTH_LONG).show();
        }

    }

//    public void saveProfileImageToFirebase(Bitmap image) {
//        // create the string of the image, which is based on this person's id
//        String imageTitleDatabase = String.format("%.png", RawrApp.getUsingUserId());
//        // convert the image first to byte array
//        byte[] imageByte = RawrImages.convertImageToByteArray(image);
//        // store image to firebase storage by first getting the reference to that image based on the user id
//        final StorageReference ref = FirebaseStorage.getInstance().getReference(imageTitleDatabase);
//        ref.putBytes(imageByte).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    // when completed, get the image url and save it to DB
//                    ref.getDownloadUrl();
//                    RequestParams params = RawrImages.getParamsSaveProfileImage(RawrApp.getUsingUserId(), ref.getDownloadUrl().toString());
//                    client.post(RawrApp.DB_URL + "/image/profile_update", params, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                            Log.e(TAG, String.format("%s", response));
//                            // TODO - then, populate wherever the image was supposed to go on success
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                            Log.e(TAG, String.format("Error in saving image url to DB: %s", errorResponse));
//                        }
//                    });
//
//                } else {
//                    // TODO - Snackbar that it failed
//                }
//            }
//
//        });

        /* public void testFirebase (Bitmap image){ // OLD FIREBASE STUFF
            // StorageReference ref = FirebaseStorage.getInstance().getReference().child("image_test.png");
            FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance(); DatabaseReference images = firebaseDB.getReference("images"); images.setValue(imageEncoded); FirebaseStorage storageRef = FirebaseStorage.getInstance("gs://air-space-images.appspot.com"); storageRef.getReference("image_test.png");
        } */
//    }

}
