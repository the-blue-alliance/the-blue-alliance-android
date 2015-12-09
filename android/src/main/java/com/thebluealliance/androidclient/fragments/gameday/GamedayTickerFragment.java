package com.thebluealliance.androidclient.fragments.gameday;

import com.thebluealliance.androidclient.fragments.FirebaseTickerFragment;

public class GamedayTickerFragment extends FirebaseTickerFragment {

    public static GamedayTickerFragment newInstance() {
        return new GamedayTickerFragment();
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected String getFirebaseUrlSuffix() {
        return "notifications/";
    }
}
