package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.fragments.ExpandableListViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictBreakdownSubscriber;

import dagger.hilt.android.AndroidEntryPoint;
import rx.Observable;

@AndroidEntryPoint
public class TeamAtDistrictBreakdownFragment
  extends ExpandableListViewFragment<DistrictRanking, TeamAtDistrictBreakdownSubscriber> {

    public static final String DISTRICT = "districtKey", TEAM = "teamKey";

    private String mTeamKey;
    private String mDistrictKey;

    public static TeamAtDistrictBreakdownFragment newInstance(String teamKey, String districtKey) {
        TeamAtDistrictBreakdownFragment f = new TeamAtDistrictBreakdownFragment();
        Bundle args = new Bundle();
        args.putString(TEAM, teamKey);
        args.putString(DISTRICT, districtKey);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTeamKey = getArguments().getString(TEAM);
            String districtKey = getArguments().getString(DISTRICT);
            if (!DistrictHelper.validateDistrictKey(districtKey)) {
                throw new IllegalArgumentException("Invalid District Key " + districtKey);
            }
            mDistrictKey = districtKey;
        }
        super.onCreate(savedInstanceState);

        mBinder.setExpandMode(ExpandableListViewBinder.MODE_EXPAND_NONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected Observable<DistrictRanking> getObservable(String cacheHeader) {
        return mDatafeed.fetchTeamAtDistrictRankings(mTeamKey, mDistrictKey, cacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamAtDistrictBreakdown_%1$s_%2$s", mTeamKey, mDistrictKey);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_assignment_black_48dp, R.string.no_team_district_breakdown);
    }
}
