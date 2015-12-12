package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

public class RankingListElement extends ListElement {

    public final int teamNumber;
    public final String teamName;
    public final int teamRank;
    public final String teamRecord;
    public final String teamBreakdown;

    public RankingListElement(String key, int number, String name, int ranking, String record, String breakdown) {
        super(key);
        teamNumber = number;
        teamName = name;
        teamRank = ranking;
        teamRecord = record;
        teamBreakdown = breakdown;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_ranking, null);

            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            holder.rank = (TextView) convertView.findViewById(R.id.team_rank);
            holder.record = (TextView) convertView.findViewById(R.id.team_record);
            holder.breakdown = (TextView) convertView.findViewById(R.id.ranking_breakdown);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText("" + teamNumber);

        if (teamName.equals("")) {
            holder.teamName.setVisibility(View.INVISIBLE);
        } else {
            holder.teamName.setText(teamName);
        }

        holder.rank.setText(String.format(c.getString(R.string.team_rank), teamRank));

        if (teamRecord.isEmpty()) {
            holder.record.setVisibility(View.GONE);
        } else {
            holder.record.setText(teamRecord);
        }

        holder.breakdown.setText(teamBreakdown);

        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
        TextView rank;
        TextView record;
        TextView breakdown;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RankingListElement)) {
            return false;
        }
        RankingListElement element = (RankingListElement) o;
        return teamNumber == element.teamNumber &&
                teamName.equals(element.teamName) &&
                teamRank == element.teamRank &&
                teamRecord.equals(element.teamRecord) &&
                teamBreakdown.equals(element.teamBreakdown);
    }
}
