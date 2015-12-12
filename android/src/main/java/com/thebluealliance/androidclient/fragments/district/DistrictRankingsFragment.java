package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.DistrictRankingsSubscriber;

import java.util.List;

import rx.Observable;

public class DistrictRankingsFragment
        extends ListViewFragment<List<DistrictTeam>, DistrictRankingsSubscriber> {

    public static final String KEY = "districtKey";

    private String mShort;
    private int mYear;

    public static DistrictRankingsFragment newInstance(String key) {
        DistrictRankingsFragment f = new DistrictRankingsFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() == null || !getArguments().containsKey(KEY)) {
            throw new IllegalArgumentException("DistrictRankingsFragment must be constructed with district key");
        }
        String key = getArguments().getString(KEY);
        if (!DistrictHelper.validateDistrictKey(key)) {
            throw new IllegalArgumentException("Invalid district key + " + key);
        }
        mShort = key.substring(4);
        mYear = Integer.parseInt(key.substring(0, 4));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<DistrictTeam>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchDistrictRankings(mShort, mYear, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("districtRankings_%1$s_%2$d", mShort, mYear);
    }

    @Override
    protected NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_ranking_data);
    }
}
