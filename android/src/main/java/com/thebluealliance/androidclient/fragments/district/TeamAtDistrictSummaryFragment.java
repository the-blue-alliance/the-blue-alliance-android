package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListvVewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictSummarySubscriber;

import rx.Observable;

public class TeamAtDistrictSummaryFragment
  extends ListvVewFragment<DistrictTeam, TeamAtDistrictSummarySubscriber> {

    public static final String DISTRICT = "districtKey", TEAM = "teamKey";

    private String mTeamKey;
    private String mDistrictShort;
    private int mYear;

    public static TeamAtDistrictSummaryFragment newInstance(String teamKey, String districtKey) {
        TeamAtDistrictSummaryFragment f = new TeamAtDistrictSummaryFragment();
        Bundle args = new Bundle();
        args.putString(TEAM, teamKey);
        args.putString(DISTRICT, districtKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String districtKey = "";
        if (getArguments() != null) {
            mTeamKey = getArguments().getString(TEAM);
            districtKey = getArguments().getString(DISTRICT);
            if (!DistrictHelper.validateDistrictKey(districtKey)) {
                throw new IllegalArgumentException("Invalid district key " + districtKey);
            }
            mDistrictShort = districtKey.substring(4);
            mYear = Integer.parseInt(districtKey.substring(0, 4));
            mSubscriber.setTeamKey(mTeamKey);
            mSubscriber.setDistrictKey(districtKey);
        }
        mDistrictShort = districtKey.substring(4);
        mYear = Integer.parseInt(districtKey.substring(0, 4));
        super.onCreate(savedInstanceState);

        mSubscriber.setTeamKey(mTeamKey);
        mSubscriber.setDistrictKey(districtKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setSelector(R.drawable.transparent);
        return view;
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<DistrictTeam> getObservable() {
        return mDatafeed.fetchTeamAtDistrictRankings(mTeamKey, mDistrictShort, mYear);
    }
}
