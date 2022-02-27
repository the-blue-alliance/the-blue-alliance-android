package com.thebluealliance.androidclient.fragments.gameday;

import com.thebluealliance.androidclient.fragments.FirebaseTickerFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GamedayTickerFragment extends FirebaseTickerFragment {

    public static GamedayTickerFragment newInstance() {
        return new GamedayTickerFragment();
    }

    @Override
    protected String getFirebaseUrlSuffix() {
        return "notifications/";
    }
}
