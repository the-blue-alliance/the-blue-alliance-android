package com.thebluealliance.androidclient.fragments.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.binders.ExpandableListViewBinder;
import com.thebluealliance.androidclient.fragments.ExpandableListViewFragment;
import com.thebluealliance.androidclient.helpers.DistrictHelper;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.NoDataViewParams;
import com.thebluealliance.androidclient.subscribers.TeamAtDistrictBreakdownSubscriber;

import rx.Observable;

public class TeamAtDistrictBreakdownFragment
  extends ExpandableListViewFragment<DistrictTeam, TeamAtDistrictBreakdownSubscriber> {

    public static final String DISTRICT = "districtKey", TEAM = "teamKey";

    private String mTeamKey;
    private String mDistrictShort;
    private int mYear;

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
            mDistrictShort = districtKey.substring(4);
            mYear = Integer.parseInt(districtKey.substring(0, 4));
        }
        super.onCreate(savedInstanceState);

        mBinder.setExpandMode(ExpandableListViewBinder.MODE_EXPAND_NONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<DistrictTeam> getObservable(String cacheHeader) {
        return mDatafeed.fetchTeamAtDistrictRankings(mTeamKey, mDistrictShort, mYear, cacheHeader);
    }

    @Override
    protected String getRefreshTag() {
        return String.format("teamAtDistrictBreakdown_%1$s_%2$s_%3$d", mTeamKey, mDistrictShort, mYear);
    }

    @Override public NoDataViewParams getNoDataParams() {
        return new NoDataViewParams(R.drawable.ic_assignment_black_48dp, R.string.no_team_district_breakdown);
    }
}
