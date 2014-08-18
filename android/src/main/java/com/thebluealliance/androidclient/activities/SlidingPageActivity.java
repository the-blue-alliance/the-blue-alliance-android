package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.thebluealliance.androidclient.R;
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
    public void setContentView(int contentLayout){
        super.setContentView(R.layout.activity_sliding_panel);

        RelativeLayout root = (RelativeLayout)findViewById(R.id.activity_content);
        LayoutInflater inflater = getLayoutInflater();
        inflater.inflate(contentLayout, root);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_panel, new NotificationSettingsFragment(), PANEL_TAG).commit();
    }
}
