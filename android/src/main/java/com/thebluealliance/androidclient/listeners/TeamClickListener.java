package com.thebluealliance.androidclient.listeners;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

public class TeamClickListener implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final Context mContext;
    private final String mTeamKey;
    private final TeamListSubscriber mSubscriber;

    /**
     * Constructor to use with {@link com.thebluealliance.androidclient.fragments.TeamListFragment}
     * Allows registering the click listener at the ListView level
     */
    public TeamClickListener(Context c, TeamListSubscriber subscriber) {
        super();
        mContext = c;
        mSubscriber = subscriber;
        mTeamKey = null;
    }

    public TeamClickListener(Context c, String teamKey) {
        super();
        mContext = c;
        mTeamKey = teamKey;
        mSubscriber = null;
    }

    @Override
    public void onClick(View v) {
        String teamKey = mTeamKey;
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            teamKey = TeamHelper.baseTeamKey(teamKey);
            /* Track the call */
            Intent intent = ViewTeamActivity.newInstance(mContext, teamKey);
            AnalyticsHelper.sendClickUpdate(mContext, "TeamListElement", teamKey, "");
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<Team> teams = mSubscriber.getApiData();
        if (teams == null) {
            return;
        }

        String teamKey = teams.get(position).getKey();
        Intent i = ViewTeamActivity.newInstance(mContext, teamKey);
        mContext.startActivity(i);

        AnalyticsHelper.sendClickUpdate(mContext, "team_click", i.getDataString(), teamKey);
    }
}
