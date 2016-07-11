package com.thebluealliance.androidclient.fragments.gameday;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.WebcastListSubscriber;

import android.os.Bundle;

import java.util.List;

import rx.Observable;

public class GamedayWebcastsFragment extends ListViewFragment<List<Event>, WebcastListSubscriber> {

    private int mYear;
    private int mWeek;

    public static GamedayWebcastsFragment newInstance() {
        return new GamedayWebcastsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mYear = Utilities.getCurrentYear();
        mWeek = Utilities.getCurrentCompWeek();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<Event>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchEventsInWeek(mYear, mWeek, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("gamedayWebcasts_%1$d_%2$d", mYear, mWeek);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_videocam_black_48dp, R.string.no_webcast_data_found);
    }
}
