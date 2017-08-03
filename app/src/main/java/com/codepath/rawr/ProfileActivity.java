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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    // views
    ImageView iv_profile_image;

    RelativeLayout parentLayout;

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

}
