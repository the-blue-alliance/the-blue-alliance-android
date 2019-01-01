package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.fragments.ListViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictSummarySubscriber;

import rx.Observable;

public class TeamAtDistrictSummaryFragment
  extends ListViewFragment<DistrictRanking, TeamAtDistrictSummarySubscriber> {

    public static final String DISTRICT = "districtKey", TEAM = "teamKey";

    private String mTeamKey;
    private String mDistrictKey;

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
            mDistrictKey = districtKey;
        }
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
    protected Observable<DistrictRanking> getObservable(String cacheHeader) {
        return mDatafeed.fetchTeamAtDistrictRankings(mTeamKey, mDistrictKey, cacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamAtDistrictSummary_%1$s_%2$s", mTeamKey, mDistrictKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_assignment_black_48dp, R.string.no_district_summary);
    }
}
