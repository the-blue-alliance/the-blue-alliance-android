package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.TeamAtDistrictClickListener;

public class DistrictTeamListElement extends ListElement {

    public final String teamKey;
    public final String districtKey;
    public final String teamName;
    public final int totalPoints;
    public final int teamRank;

    public DistrictTeamListElement(String teamKey, String districtKey, String teamName, int rank, int points) {
        super();
        this.teamKey = teamKey;
        this.districtKey = districtKey;
        this.teamName = teamName;
        this.totalPoints = points;
        this.teamRank = rank;
    }

    public DistrictTeamListElement(String teamKey, String districtKey, int rank, int points) {
        super();
        this.teamKey = teamKey;
        this.districtKey = districtKey;
        this.teamName = "";
        this.totalPoints = points;
        this.teamRank = rank;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_district_ranking, null);
            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamRank = (TextView) convertView.findViewById(R.id.team_rank);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            holder.totalPoints = (TextView) convertView.findViewById(R.id.total_points);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText(teamKey.substring(3));
        holder.teamRank.setText(String.format(c.getString(R.string.team_rank), teamRank));
        if (teamName.isEmpty()) {
            holder.teamName.setText("Team " + teamKey.substring(3));
        } else {
            holder.teamName.setText(teamName);
        }
        holder.totalPoints.setText(String.format(c.getString(R.string.district_points_format), totalPoints));
        if (districtKey != null && !districtKey.isEmpty()) {
            convertView.setOnClickListener(new TeamAtDistrictClickListener(c, teamKey, districtKey));
        } else {
            convertView.setBackgroundResource(R.drawable.transparent);
        }
        return convertView;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DistrictTeamListElement)) {
            return false;
        }
        DistrictTeamListElement element = (DistrictTeamListElement) o;
        return teamKey.equals(element.teamKey) &&
                districtKey.equals(element.districtKey) &&
                teamName.equals(element.teamName) &&
                totalPoints == element.totalPoints &&
                teamRank == element.teamRank;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamRank;
        TextView teamName;
        TextView totalPoints;
    }
}
