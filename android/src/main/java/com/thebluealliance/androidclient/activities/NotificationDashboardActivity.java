package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.NotificationDashboardFragment;

/**
 * Created by phil on 2/3/15.
 */
public class NotificationDashboardActivity extends RefreshableHostActivity {

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.LOG_TAG, "Created Dashboard Activity");
        setContentView(R.layout.activity_notification_dashboard);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setupActionBar();
        
        Fragment fragment = new NotificationDashboardFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support).replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
    }

    private void setupActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Recent Notifications"); //TODO make string resource
    }
    
    @Override
    public void showWarningMessage(String message) {
        /* Noope */
    }

    @Override
    public void hideWarningMessage() {
        /* Noope */
    }
}
