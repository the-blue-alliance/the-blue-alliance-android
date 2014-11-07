package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.accounts.AccountHelper;
import com.thebluealliance.androidclient.fragments.mytba.NotificationSettingsFragment;

/**
 * File created by phil on 8/18/14.
 */
public abstract class SlidingPageActivity extends RefreshableHostActivity {

    private static final String PANEL_TAG = "sliding_page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int contentLayout) {
        if (AccountHelper.isMyTBAEnabled(this)) {
            super.setContentView(R.layout.activity_sliding_panel);

            RelativeLayout root = (RelativeLayout) findViewById(R.id.activity_content);
            LayoutInflater inflater = getLayoutInflater();
            inflater.inflate(contentLayout, root);

            Log.d(Constants.LOG_TAG, "Model: " + modelKey);
            //getSupportFragmentManager().beginTransaction().replace(R.id.activity_panel, NotificationSettingsFragment.newInstance(modelKey), PANEL_TAG).commit();
        } else {
            super.setContentView(contentLayout);
        }
    }
}
