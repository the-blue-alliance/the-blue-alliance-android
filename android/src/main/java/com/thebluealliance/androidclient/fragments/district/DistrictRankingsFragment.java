package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.DistrictRankingsSubscriber;

import java.util.List;

import rx.Observable;

public class DistrictRankingsFragment
  extends ListViewFragment<List<DistrictRanking>, DistrictRankingsSubscriber> {

    public static final String KEY = "districtKey";

    private String mDistrictKey;

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
        mDistrictKey = key;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<List<DistrictRanking>> getObservable(String tbaCacheHeader) {
        return mDatafeed.fetchDistrictRankings(mDistrictKey, tbaCacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("districtRankings_%1$s", mDistrictKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_poll_black_48dp, R.string.no_ranking_data);
    }
}
