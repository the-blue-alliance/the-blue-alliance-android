package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.fragments.RecentNotificationsFragment;

/**
 * Created by phil on 2/3/15.
 */
public class RecentNotificationsActivity extends RefreshableHostActivity {

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";
    
    public static Intent newInstance(Context c) {
        return new Intent(c, RecentNotificationsActivity.class);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(toolbar);
        setupActionBar();
        
        Fragment fragment = new RecentNotificationsFragment();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_support, R.anim.fade_out_support).replace(R.id.container, fragment, MAIN_FRAGMENT_TAG).commit();
    }

    @Override
    public void onNavigationDrawerCreated() {
        setNavigationDrawerItemSelected(R.id.nav_item_notifications);
    }

    private void setupActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle(getString(R.string.recent_notifications_title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.recent_notifications_help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recent_notifications_help:
                Utilities.showHelpDialog(this, R.raw.recent_notifications_help, getString(R.string.action_help));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
