package com.thebluealliance.androidclient.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.thebluealliance.androidclient.R;

/**
 * Created by Nathan on 5/14/2014.
 */
public class ViewMatchActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String VIDEO_FRAGMENT_TAG = "videoFragment";

    YouTubePlayerFragment mPlayerFragment;
    YouTubePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);
        mPlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        mPlayerFragment.initialize("AIzaSyAmk9Hono2mQQlvTrFvUwk1OcrfMG812N4", this);
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
}
