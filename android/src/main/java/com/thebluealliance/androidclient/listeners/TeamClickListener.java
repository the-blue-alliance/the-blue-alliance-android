package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.subscribers.TeamListSubscriber;

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
            if (TeamHelper.validateMultiTeamKey(teamKey)) {
                // Take out extra letter at end to make team key valid.
                teamKey = teamKey.substring(0, teamKey.length() - 1);
            }
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
