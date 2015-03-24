package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.thebluealliance.androidclient.Analytics;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.AnalyticsHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 4/23/14.
 */
public class TeamListElement extends ListElement {

    private int mTeamNumber;
    private String mTeamName;
    private String mTeamLocation;
    private boolean mShowLinkToTeamDetails = false;

    public TeamListElement(Team team) throws BasicModel.FieldNotDefinedException {
        super(team.getTeamKey());
        mTeamNumber = team.getTeamNumber();
        mTeamName = team.getNickname();
        mTeamLocation = team.getLocation();
    }

    public TeamListElement(String key, int number, String name, String location) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamLocation = location;
    }

    public TeamListElement(String key, int number, String name, String location, boolean showLinkToTeamDetails) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamLocation = location;
        mShowLinkToTeamDetails = showLinkToTeamDetails;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_team, null);

            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            holder.teamLocation = (TextView) convertView.findViewById(R.id.team_location);
            holder.teamInfo = (ImageView) convertView.findViewById(R.id.team_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText("" + mTeamNumber);

        if (mTeamName.isEmpty()) {
            holder.teamName.setText("Team " + mTeamNumber);
        } else {
            holder.teamName.setText(mTeamName);
        }

        holder.teamLocation.setText(mTeamLocation);

        if (mShowLinkToTeamDetails) {
            holder.teamInfo.setVisibility(View.VISIBLE);
            holder.teamInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String teamKey = "frc" + mTeamNumber;
                    Intent intent = ViewTeamActivity.newInstance(context, teamKey);
                    
                    /* Track the call */
                    AnalyticsHelper.sendClickUpdate(c, "team_click", "TeamListElement", "");

                    context.startActivity(intent);
                }
            });
        } else {
            holder.teamInfo.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
        TextView teamLocation;
        ImageView teamInfo;
    }
}
