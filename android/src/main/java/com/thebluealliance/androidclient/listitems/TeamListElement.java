package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.models.Team;

/**
 * File created by phil on 4/23/14.
 */
public class TeamListElement extends ListElement {

    private int mTeamNumber;
    private String mTeamName;
    private String mTeamLocation;
    private boolean mShowLinkToTeamDetails = false;

    public TeamListElement(Team team) {
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
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_team, null);
            view.setTag(key);
            view.setSelected(selected);

            TextView title = (TextView) view.findViewById(R.id.team_number);
            title.setText("" + mTeamNumber);

            TextView dates = (TextView) view.findViewById(R.id.team_name);
            if (mTeamName.equals("")) {
                dates.setText("Team " + mTeamNumber);
            } else {
                dates.setText(mTeamName);
            }

            TextView location = (TextView) view.findViewById(R.id.team_location);
            location.setText(mTeamLocation);

            ImageView info = (ImageView) view.findViewById(R.id.team_info);
            if(mShowLinkToTeamDetails) {
                info.setVisibility(View.VISIBLE);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = ViewTeamActivity.newInstance(context, "frc" + mTeamNumber);
                        context.startActivity(intent);
                    }
                });
            } else {
                info.setVisibility(View.GONE);
            }
        }
        return view;
    }
}
