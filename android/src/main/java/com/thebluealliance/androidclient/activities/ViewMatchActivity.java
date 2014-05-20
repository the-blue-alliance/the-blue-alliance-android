package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.match.PopulateMatchInfo;

/**
 * Created by Nathan on 5/14/2014.
 */
public class ViewMatchActivity extends RefreshableHostActivity implements YouTubePlayer.OnInitializedListener {

    public static final String MATCH_KEY = "match_key";

    private static final String VIDEO_FRAGMENT_TAG = "videoFragment";

    private String mMatchKey;

    YouTubePlayerFragment mPlayerFragment;
    YouTubePlayer mPlayer;

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

        mPlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        mPlayerFragment.initialize("AIzaSyAmk9Hono2mQQlvTrFvUwk1OcrfMG812N4", this);
        new PopulateMatchInfo(this).execute(mMatchKey);
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("");
    }

    @Override
    public void showWarningMessage(String message) {

    }

    @Override
    public void hideWarningMessage() {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        mPlayer = player;
        if (!wasRestored) {
            mPlayer.cueVideo("dQw4w9WgXcQ");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {

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
