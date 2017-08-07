package com.codepath.rawr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.rawr.models.RawrImages;
import com.codepath.rawr.models.ShippingRequest;
import com.codepath.rawr.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class SenderFormActivity extends AppCompatActivity {
    // create variables and views
    public CheckBox cb_envelope, cb_smallBox, cb_largeBox, cb_clothing, cb_other, cb_liquid, cb_fragile;
    public boolean item_envelope, item_smallBox, item_largeBox, item_clothing, item_other, item_liquid, item_fragile = false;
    public EditText et_dropoff, et_pickup, et_name, et_other, et_phone, et_email, et_payment, et_details;
    public Button bt_confirm, bt_photo_upload;
    public ImageView iv_item;
    public TextView tv_file_title;
    // picture variables
    public Bitmap requestPicture;
    public boolean pictureUploaded = false;

    // for debugging and snackbar
    public final static String TAG = "S:SenderFormActivity";
    public RelativeLayout parentLayout;

    // to get the user and on activity result for where this comes from
    public User usingUser;
    public Intent resultIntent;
    public int action;

    // for server
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_form);

        // get stuff needed for db
        client = new AsyncHttpClient();
        action = getIntent().getIntExtra("action", 0);
        resultIntent = new Intent();

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
        et_email = (EditText) findViewById(R.id.tv_tvlr_email);
        et_phone = (EditText) findViewById(R.id.et_no);
        et_payment = (EditText) findViewById(R.id.et_payment);
        et_details = (EditText) findViewById(R.id.et_details);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bt_photo_upload = (Button) findViewById(R.id.bt_photo_upload);
        iv_item = (ImageView) findViewById(R.id.iv_itemRequestedPhoto);
        tv_file_title = (TextView) findViewById(R.id.tv_file_title);

        // get the parent layout
        parentLayout = (RelativeLayout) findViewById(R.id.relativeLayoutInitSenderForm);

        // make the button deactivated until we get the user using the app
        bt_confirm.setEnabled(false);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        // get the user using the app, which activates the button
        getUsingUser();


        bt_photo_upload.setOnClickListener(new View.OnClickListener() {
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

    public void disableSubmitButton() {
        // TODO - set progress bar visible here!!!
        bt_confirm.setEnabled(false);
    }
    public void enableSubmitButton() {
        // TODO - set progress bar invisible here
        bt_confirm.setEnabled(true);
    }

    public int getItemTotal() {
        // this implies that the person has to choose at least one item!
        int res = 0;
        boolean[] itemBools = new boolean[]{cb_envelope.isChecked(), cb_smallBox.isChecked(), cb_largeBox.isChecked(), cb_clothing.isChecked(), cb_fragile.isChecked(), cb_liquid.isChecked(), cb_other.isChecked()};
        for (int i = 0; i < itemBools.length; i++) {
            if (itemBools[i]) res += 1;
        }
        return res;
    }

    public RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.put("travel_notice_id", getIntent().getExtras().getString("travel_notice_id"));
        params.put("ruid", RawrApp.getUsingUserId());
        params.put("action", action);

        params.put("item_envelopes", cb_envelope.isChecked());
        params.put("item_smbox", cb_smallBox.isChecked());
        params.put("item_lgbox", cb_largeBox.isChecked());
        params.put("item_clothing", cb_clothing.isChecked());
        params.put("item_fragile", cb_fragile.isChecked());
        params.put("item_liquid", cb_liquid.isChecked());
        params.put("item_other", cb_other.isChecked());
        params.put("item_other_name", et_other.getText().toString());
        params.put("drop_off_flexibility", et_dropoff.getText().toString());
        params.put("pick_up_flexibility", et_pickup.getText().toString());

        if (action == 0) {
            // meaning sending
            params.put("recipient_name", et_name.getText().toString());
            params.put("recipient_phone", et_phone.getText().toString());
            params.put("recipient_email", et_email.getText().toString());
            params.put("recipient_uses_app", false);

            params.put("deliverer_name", usingUser.getFullName());
            params.put("deliverer_email", usingUser.email);
            params.put("deliverer_phone", usingUser.phone);
            params.put("deliverer_uses_app", true);
        } else if (action == 1) {
            // meaning receiving
            params.put("deliverer_name", et_name.getText().toString());
            params.put("deliverer_phone", et_phone.getText().toString());
            params.put("deliverer_email", et_email.getText().toString());
            params.put("deliverer_uses_app", false);

            params.put("recipient_name", usingUser.getFullName());
            params.put("recipient_email", usingUser.email);
            params.put("recipient_phone", usingUser.phone);
            params.put("recipient_uses_app", true);
        }

        params.put("item_total", getItemTotal()); // TODO - CHANGE ITEM TOTAL TO SOMETHING
        return params;
    }

    public boolean isRecipientFilled() {
        // checks if these information are empty or not with the .isEmpty() string method and returns the opposite of that (logic here haha)
        boolean b1 = !(et_name.getText().toString().equals(null) || et_phone.getText().toString().equals(null) || et_email.getText().toString().equals(null));
        return b1 && !(et_name.getText().toString().isEmpty() || et_phone.getText().toString().isEmpty() || et_email.getText().toString().isEmpty());
    }

    public void sendRequest() {
        // we disable the button so that one does not send too many requests at the same time
        disableSubmitButton();
        RequestParams params = getParams();

        if (!pictureUploaded) {
            Log.e(TAG, "Picture is not uploaded");
            Snackbar.make(parentLayout, "You must upload a picture", Snackbar.LENGTH_LONG).show();
            enableSubmitButton();
        } else if (getItemTotal() == 0) {
            // don't send request and tell user that he has to pick at least one checkbox
            Log.e(TAG, "item total is 0");
            Snackbar.make(parentLayout, "You must select at least one checkbox.", Snackbar.LENGTH_LONG).show();
            enableSubmitButton();
        } else if (!isRecipientFilled()) {
            Log.e(TAG, "Recipient info not filled up");
            // checks if the recipient's informations is filled up because the server will also throw an error if it's not
            Snackbar.make(parentLayout, "You must fill up all of the recipient's details.", Snackbar.LENGTH_LONG).show();
            enableSubmitButton();
        } else {
            client.post(RawrApp.DB_URL + "/request/send", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // if request sent successfully, set the result ok
                    try {
                        // get the response id
                        ShippingRequest request = ShippingRequest.fromJSONServer(response.getJSONObject("request"), response.getJSONObject("travel_notice"), response.getJSONObject("user"));
                        saveItemImageToFirebase(requestPicture, request.id, request);
                    } catch (JSONException e) {
                        // JSON ERROR occurred
                        Log.e(TAG, String.format("%s", e));
                        resultIntent.putExtra("message", "JSON ERROR: failure uploading picture");
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }

                // TODO - One of the onFailure may say that the request has already been sent. We have to put that in the resultIntent as message
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                    // if an error occurred, set result cancelled
                    if (errorResponse != null) {
                        try {
                            String msg = errorResponse.getString("message");
                            resultIntent.putExtra("message", msg);
                            setResult(RESULT_CANCELED, resultIntent); finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resultIntent.putExtra("message", "Error (1) in endpoint request_send");
                            setResult(RESULT_CANCELED, resultIntent); finish();
                        }
                    } else {
                        resultIntent.putExtra("message", "Error (1) in endpoint request_send");
                        setResult(RESULT_CANCELED, resultIntent); finish();
                    }
                }
            });
        }
    }

    public void getUsingUser() {
        // make a call to server to get the user and then create userProfile base on that json from the server
        RequestParams params = new RequestParams();
        params.put("uid", RawrApp.getUsingUserId());
        client.get(RawrApp.DB_URL + "/user/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // populate the userProfile from the JSON received here, then enable the bt_confirm
                    usingUser = User.fromJSONServer(response.getJSONObject("data"));
                    bt_confirm.setEnabled(true);
                } catch (JSONException e) {
                    Log.e(TAG, String.format("Parsing JSON excepted %s", e));
                    Log.e(TAG, String.format("User is not gotten, JSON parsing error: %s", e));
                    // if an error occurred, set result cancelled because we need the user!
                    resultIntent.putExtra("message", "Error in parsing user JSON from the server");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                Log.e(TAG, String.format("User not gotten error 1 %s", errorResponse));
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("CODE: %s ERROR: %s", statusCode, errorResponse));
                Log.e(TAG, String.format("User not gotten error 2 %s", errorResponse));
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("%s", responseString));
                Log.e(TAG, String.format("User not gotten error 3 %s", responseString));
                // if an error occurred, set result cancelled because we need the user!
                resultIntent.putExtra("message", "Error in getting user from server");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("message", String.format("Cancelled"));
        setResult(RESULT_CANCELED, data); finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            // for loading images, this ma
            try {
                // get the image from the cellphone
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                // set request picture to this new bitmap, and then set picture uploaded to true
                requestPicture = BitmapFactory.decodeStream(imageStream); // Bitmaps are the ones to be placed/replaced in imageViews
                // TODO - Check file size before setting that to true
                pictureUploaded = true;

                // convert image to bytes
                byte[] imageByte = RawrImages.convertImageToByteArray(requestPicture);
                iv_item.setImageBitmap(requestPicture);
                iv_item.setVisibility(View.VISIBLE);
                tv_file_title.setText("1 photo uploaded");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("Bitmap error! %s", e));
            }
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            Snackbar.make(parentLayout, "Cancelled loading picture image", Snackbar.LENGTH_LONG).show();
        }
    }

    public void saveItemImageToFirebase(Bitmap image, String requestId, final ShippingRequest request) {
        // create the string of the image, which is based on this person's id
        String imageTitleDatabase = requestId + ".png";
        // convert the image first to byte array
        byte[] imageByte = RawrImages.convertImageToByteArray(image);
        // store image to firebase storage by first getting the reference to that image based on the user id
        final StorageReference ref = FirebaseStorage.getInstance().getReference(imageTitleDatabase);
        ref.putBytes(imageByte).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // when completed, get the image url and save it to DB
                    resultIntent.putExtra("message", "success");
                    enableSubmitButton();
                    setResult(RESULT_OK, resultIntent); finish();
                } else {
                    resultIntent.putExtra("message", "FIREBASE ERROR: failure uploading picture");
                    enableSubmitButton();
                    setResult(RESULT_OK, resultIntent); finish();
                }
            }

        });
    }
}


