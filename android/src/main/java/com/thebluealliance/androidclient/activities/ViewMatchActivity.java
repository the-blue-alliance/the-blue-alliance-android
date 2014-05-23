package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.match.PopulateMatchInfo;

/**
 * Created by Nathan on 5/14/2014.
 */
public class ViewMatchActivity extends RefreshableHostActivity {

    public static final String MATCH_KEY = "match_key";

    private static final String VIDEO_FRAGMENT_TAG = "videoFragment";

    private String mMatchKey;

    private TextView warningMessage;

    public static Intent newInstance(Context context, String matchKey) {
        Intent intent = new Intent(context, ViewMatchActivity.class);
        intent.putExtra(MATCH_KEY, matchKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);
        setupActionBar();

        mMatchKey = getIntent().getStringExtra(MATCH_KEY);
        if (mMatchKey == null) {
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }

        warningMessage = (TextView) findViewById(R.id.warning_container);

        new PopulateMatchInfo(this).execute(mMatchKey);
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Match");
    }

    @Override
    public void showWarningMessage(String message) {
        warningMessage.setText(message);
        warningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        warningMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
