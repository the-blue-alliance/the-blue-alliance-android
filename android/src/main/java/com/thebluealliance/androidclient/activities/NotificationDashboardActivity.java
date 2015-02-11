package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.fragments.NotificationDashboardFragment;

/**
 * Created by phil on 2/3/15.
 */
public class NotificationDashboardActivity extends RefreshableHostActivity {

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    public static Intent newInstance(Context c) {
        return new Intent(c, NotificationDashboardActivity.class);
    }
    
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
        setActionBarTitle(getString(R.string.notification_dashboard_title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.notification_dash_help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.dash_help:
                Utilities.showHelpDialog(this, R.raw.notification_dash_help, getString(R.string.action_help));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
