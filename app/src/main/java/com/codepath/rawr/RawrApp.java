package com.codepath.rawr;

import android.app.Application;
import android.content.Context;

/**
 * Created by robertvunabandi on 7/25/17.
 */

public class RawrApp extends Application {
    private static Context context;
    private static String usingUserId;
    public static String tempIdNew = "5977a6ca44f8d217b87f7819";
    public static String tempIdOld = "5977a6de44f8d217b87f781a";
    public static final int ADDITIONAL_DETAILS_CODE = 0;
    public static final int CODE_REQUESTER_FORMS_ACTIVITY = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        RawrApp.context = this;
        tempIdNew = getString(R.string.temporary_user_id_new);
        tempIdOld = getString(R.string.temporary_user_id_old);
        // TODO - make using user be equal to something saved in sharedpreferences
        // usingUserId
    }

    public static String getUsingUserId() {
        // TODO - make this actually get the person who is using the app or return null through shared preferences
        return tempIdNew;
        // return usingUserId; // this may be empty/null
    }
}
