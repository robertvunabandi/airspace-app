package com.codepath.rawr;

import android.app.Application;
import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by robertvunabandi on 7/25/17.
 */

public class RawrApp extends Application {
    private static Context context;
    private static String usingUserId;
    public static String tempId = "INVALIDID";
    public static final int ADDITIONAL_DETAILS_CODE = 0;
    public static final int CODE_REQUESTER_FORMS_ACTIVITY = 1;
    public static final int TRAVEL_PENDING_REQUESTS_CODE = 2;
    public static final int TRAVEL_ACCEPTED_REQUESTS_CODE = 3;
    public static final int UPDATE_ADDITIONAL_DETAILS_CODE = 4;
    public static final int CODE_LOAD_PROFILE_IMAGE = 5;
    public static final String DB_URL = "http://mysterious-headland-54722.herokuapp.com";

    @Override
    public void onCreate() {
        super.onCreate();
        RawrApp.context = this;
    }

    public static String getUsingUserId() {
         return usingUserId; // this may be empty/null
    }

    public static void setUsingUserId(String id) throws Exception {
        // sets the id of the using user
        if (id.length() > 20) {
            usingUserId = id;
        } else throw new Exception("Invalid id for replacement");
    }

    /**
     * To get an image with this, use glide like this:
     *
     * View view = findViewById(R.id.<Id of the view>); // view where to store the image
     * String id = <Id of either user or request>; // id of either user or request
     *
     * StorageReference ref = getStorageReferenceForImageFromFirebase(id);
     * Glide.with(this)
     *      .using(new FirebaseImageLoaded())
     *      .load(ref)
     *      .placeholder(R.drawable.<Reference to a placeholder drawable>)
     *      .error(R.drawable.<Reference to a placeholder ERROR drawable>)
     *      .into(view);
     *
     * MAKE SURE TO ALWAYS HAVE ERROR AND PLACEHOLDER
     * */
    public static StorageReference getStorageReferenceForImageFromFirebase(String id) {
        return FirebaseStorage.getInstance().getReference(id+".png");
    }

    public static boolean isStringEmpty(String s) {
        return !(s != null && !s.equals(""));
    }

}
