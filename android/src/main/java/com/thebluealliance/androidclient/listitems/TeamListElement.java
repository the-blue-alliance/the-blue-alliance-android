package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.ModelSettingsClickListener;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.ModelType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TeamListElement extends ListElement {

    public final int mTeamNumber;
    public final String mTeamName;
    public final String mTeamLocation;
    public final boolean mShowLinkToTeamDetails;
    public final boolean mShowMyTbaDetails;

    public TeamListElement(Team team) throws BasicModel.FieldNotDefinedException {
        super(team.getKey());
        mTeamNumber = team.getNumber();
        mTeamName = team.getNickname();
        mTeamLocation = team.getLocation();
        mShowLinkToTeamDetails = false;
        mShowMyTbaDetails = false;
    }

    public TeamListElement(
      String key,
      int number,
      String name,
      String location,
      boolean showLinkToTeamDetails,
      boolean showMyTbaDetails) {
        super(key);
        mTeamNumber = number;
        mTeamName = name;
        mTeamLocation = location;
        mShowLinkToTeamDetails = showLinkToTeamDetails;
        mShowMyTbaDetails = showMyTbaDetails;
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
            holder.myTbaSettings = (ImageView) convertView.findViewById(R.id.model_settings);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText(String.format("%1$d", mTeamNumber));

        if (mTeamName.isEmpty()) {
            holder.teamName.setText(String.format("Team %1$s", mTeamNumber));
        } else {
            holder.teamName.setText(mTeamName);
        }

        holder.teamLocation.setText(mTeamLocation);

        if (mShowLinkToTeamDetails) {
            holder.teamInfo.setVisibility(View.VISIBLE);
            holder.teamInfo.setOnClickListener(new TeamClickListener(context, getKey()));
        } else {
            holder.teamInfo.setVisibility(View.GONE);
        }

        if (mShowMyTbaDetails) {
            holder.myTbaSettings.setVisibility(View.VISIBLE);
            holder.myTbaSettings.setOnClickListener(new ModelSettingsClickListener(context, getKey(), ModelType.TEAM));
            convertView.setOnClickListener(new TeamClickListener(context, getKey()));
        } else {
            holder.myTbaSettings.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
        TextView teamLocation;
        ImageView teamInfo;
        ImageView myTbaSettings;
    }
}
