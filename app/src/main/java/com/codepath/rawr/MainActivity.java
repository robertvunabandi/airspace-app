package com.codepath.rawr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.rawr.adapters.MainPagerAdapter;
import com.codepath.rawr.fragments.ConversationsFragment;
import com.codepath.rawr.fragments.SendReceiveFragment;
import com.codepath.rawr.fragments.TravelFragment;
import com.codepath.rawr.models.RawrImages;
import com.codepath.rawr.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.codepath.rawr.R.id.drawerLayout;

public class MainActivity extends AppCompatActivity {

    // set up for navigation drawer
    private String[] mDrawerTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private NavigationView navigationView;


    // setting up the views pager for fragments
    public ViewPager vpPager;
    public MainPagerAdapter pagerAdapter;
    public LinearLayout parentLayout;
    public TabLayout tabLayout;
    Context context;
    // other views
    ProgressBar pb;
    ImageView optionsButton;

    TextView text1;
    // db
    AsyncHttpClient client;
    public FirebaseAuth mAuth;
    public User usingUser;
    // for login and shared preferences
    SharedPreferences sharedPref;
    SharedPreferences.Editor spEditor;
    String user_id;
    // debugging
    private static final String TAG = "MainActivityTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        context = this;
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);

        // get server stuffs
        client = new AsyncHttpClient();

        // get the views
        pb = (ProgressBar) findViewById(R.id.progressBarMainActivity);
        optionsButton = (ImageView) findViewById(R.id.optionsButton);

        // check if the user is logged in with the SharedPreferences, first get shared pref
        sharedPref = context.getSharedPreferences(getString(R.string.sp_file_key), Context.MODE_PRIVATE);

        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        // create the pager adapter
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        // set the adapter for the pager
        vpPager.setAdapter(pagerAdapter);
        // setup the TabLayout to use the view pager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        // sets the tab icons
        setTabIcons();
        logFirebaseImageSaver();


        // TODO - Make option button actually do what it's supposed to do, include logout inside of it
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
//                logoutUser();
            }
        });

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment
                    case R.id.first:
                        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(i);
                        break;
                    case R.id.second:
                        Intent j = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(j);
                        break;
                    case R.id.third:
                        logoutUser();
                }
                return false;
            }
        });
    }

    /**
     * LOGIC FOR IMAGES TESTING
     */

    public void getImageFromAlbum() {
        // starts an intent for
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RawrApp.CODE_LOAD_PROFILE_IMAGE);
    }

    public void logFirebaseImageSaver() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(getString(R.string.firebase_email), getString(R.string.firebase_password)).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "********* Firebase image saver is good *********");
                    // not sure with this
                    // FirebaseUser user = mAuth.getCurrentUser(); updateUI(user);
                } else {
                    Log.e(TAG, "********* Firebase image saver is not good :( *********");
                }
            }
        });
    }

    public void saveProfileImageToFirebase(Bitmap image) {
        // create the string of the image, which is based on this person's id
        String imageTitleDatabase = String.format("%.png", RawrApp.getUsingUserId());
        // convert the image first to byte array
        byte[] imageByte = RawrImages.convertImageToByteArray(image);
        // store image to firebase storage by first getting the reference to that image based on the user id
        final StorageReference ref = FirebaseStorage.getInstance().getReference(imageTitleDatabase);
        ref.putBytes(imageByte).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // when completed, get the image url and save it to DB
                    ref.getDownloadUrl();
                    // TODO - Do something if task is successful

                } else {
                    // TODO - Snackbar that it failed
                }
            }

        });

        /* public void testFirebase (Bitmap image){ // OLD FIREBASE STUFF
            // StorageReference ref = FirebaseStorage.getInstance().getReference().child("image_test.png");
            FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance(); DatabaseReference images = firebaseDB.getReference("images"); images.setValue(imageEncoded); FirebaseStorage storageRef = FirebaseStorage.getInstance("gs://air-space-images.appspot.com"); storageRef.getReference("image_test.png");
        } */
    }

    /**
     * LOGIC FOR SETTING TAB ICONS
     */

    public void setTabIcons() {
        /* this makes images bigger but causes some issues */
        View ic_flight = getLayoutInflater().inflate(R.layout.customtab, null);
        ((ImageView) ic_flight.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight);
        ((TextView) ic_flight.findViewById(R.id.tv_text_icon)).setText(getString(R.string.travel));
        // set the notification indicator to invisible
        ic_flight.findViewById(R.id.rl_tab_notification_indicator).setVisibility(View.INVISIBLE);
        tabLayout.getTabAt(0).setCustomView(ic_flight);
        View ic_suitcase = getLayoutInflater().inflate(R.layout.customtab, null);
        ((ImageView) ic_suitcase.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase);
        ((TextView) ic_suitcase.findViewById(R.id.tv_text_icon)).setText(getString(R.string.send_receive));
        // set the notification indicator to invisible
        ic_suitcase.findViewById(R.id.rl_tab_notification_indicator).setVisibility(View.INVISIBLE);
        tabLayout.getTabAt(1).setCustomView(ic_suitcase);
        View ic_chats = getLayoutInflater().inflate(R.layout.customtab, null);
        ((ImageView) ic_chats.findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats);
        ((TextView) ic_chats.findViewById(R.id.tv_text_icon)).setText(getString(R.string.chats));
        // set the notification indicator to invisible
        ic_chats.findViewById(R.id.rl_tab_notification_indicator).setVisibility(View.INVISIBLE);
        tabLayout.getTabAt(2).setCustomView(ic_chats);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vpPager) {
            public void changeColorTab(TabLayout.Tab tab, int color) {
                // color 0: dark, 1: white
                int white = ContextCompat.getColor(context, R.color.White);
                int dark = ContextCompat.getColor(context, R.color.SXDark);

                // ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                // logic to change the background to white or black
                Drawable bkg = tab.getCustomView().findViewById(R.id.iv_tab_icon).getBackground();
                if (color == 1) {
                    // ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).getDrawable().setTint(white);
                    if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_flight).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight_white);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_suitcase).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase_white);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_chats).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats_white);
                    }
                    ((TextView) tab.getCustomView().findViewById(R.id.tv_text_icon)).setTextColor(white);
                } else {
                    // ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).getDrawable().setTint(dark);
                    if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_flight_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_flight);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_suitcase_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_suitcase);
                    } else if (bkg.getConstantState().equals(getResources().getDrawable(R.drawable.ic_chats_white).getConstantState())) {
                        ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setBackgroundResource(R.drawable.ic_chats);
                    }
                    ((TextView) tab.getCustomView().findViewById(R.id.tv_text_icon)).setTextColor(dark);
                }
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                changeColorTab(tab, 1);
                if (tab.getPosition() == 2) {
                    // refresh the notifications
                    ((ConversationsFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).getNotifications();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.SXDark);
                changeColorTab(tab, 0);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.White);
                changeColorTab(tab, 1);
            }
        });
        vpPager.setCurrentItem(1);
    }

    public void updateNotificationIndicator(int tabIndex, int notificationCount) {
        // safety check
        if (tabIndex > 0 && tabIndex < 3) {
            if (notificationCount > 0) {
                tabLayout.getTabAt(tabIndex).getCustomView().findViewById(R.id.rl_tab_notification_indicator).setVisibility(View.VISIBLE);
                // a string bigger than 9 is a lot so we just put 9+
                String newText = notificationCount > 9 ? "9+" : String.valueOf(notificationCount);
                ((TextView) tabLayout.getTabAt(tabIndex).getCustomView().findViewById(R.id.tv_tab_notification_indicator)).setText(newText);
            } else {
                tabLayout.getTabAt(tabIndex).getCustomView().findViewById(R.id.rl_tab_notification_indicator).setVisibility(View.INVISIBLE);
                ((TextView) tabLayout.getTabAt(tabIndex).getCustomView().findViewById(R.id.tv_tab_notification_indicator)).setText(String.valueOf(0));
            }
        }

    }

    public void logoutUser() {
        /** This is for both debugging and for actually loging a user out. It unlogs the user and then make them log in again by
         * removing the shared preference */
        spEditor = sharedPref.edit();
        spEditor.remove(getString(R.string.sp_string_user_id_key));
        spEditor.apply();
        // log the user out
        launchLogoutActivity(null);
    }

    public void setProgressVisible() {
        pb.setVisibility(View.VISIBLE);
    }

    public void setProgressDead() {
        pb.setVisibility(View.GONE);
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

    /**
     * on activity result for various things
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == RawrApp.ADDITIONAL_DETAILS_CODE) {
            // success snackbar
            ((TravelFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).getTripsData();
            snackbarCallLong("Your travel notice has been saved");
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.ADDITIONAL_DETAILS_CODE) {
            // failure snackbar
            snackbarCallIndefinite(data.getStringExtra("message"));
        } else if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // success snackbar
            snackbarCallLong("Your request has been sent.");
            // clear the fragment texts and refresh requests
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).clearViews();
            ((SendReceiveFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).refreshRequests();
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.CODE_REQUESTER_FORMS_ACTIVITY) {
            // failure snackbar
            snackbarCallIndefinite(data.getStringExtra("message"));
        } else if (resultCode == RESULT_OK && requestCode == RawrApp.UPDATE_ADDITIONAL_DETAILS_CODE) {
            String msg;
            try {
                msg = data.getExtras().getString("message");
            } catch (Exception e) {
                msg = "Message not received from activity in MainActivity.onActivityResult from UPDATE_ADDITIONAL_DETAILS_CODE";
                Log.e(TAG, msg);
            }
            snackbarCallLong(msg);
            ((TravelFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).getTripsData();
        } else if (resultCode == RESULT_CANCELED && requestCode == RawrApp.UPDATE_ADDITIONAL_DETAILS_CODE) {
            snackbarCallIndefinite(data.getStringExtra("message"));
        } else if (resultCode == RESULT_OK && requestCode == RawrApp.CODE_LOAD_PROFILE_IMAGE) {
            // for loading images, this ma
            try {
                // get the image from the cellphone
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream); // Bitmaps are the ones to be placed/replaced in imageViews
                // convert image to bytes
                byte[] imageByte = RawrImages.convertImageToByteArray(selectedImage);
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
            snackbarCallLong("Cancelled loading profile image");
        }
    }

    /**
     * All of the following are login logics
     */
    public void launchLogoutActivity(String message) {
        setProgressDead();
        Intent logoutActivity = new Intent(MainActivity.this, LogoutActivity.class);
        logoutActivity.putExtra("message", message);
        startActivity(logoutActivity);
        // finishes this activity so that we don't go back to in onBackPressed
        MainActivity.this.finishAffinity();
    }
}