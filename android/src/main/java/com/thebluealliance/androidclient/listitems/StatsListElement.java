package com.thebluealliance.androidclient.listitems;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.ThreadSafeFormatters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class StatsListElement extends ListElement {

    public final String teamNumber;
    public final String teamName;
    public final String teamStat;
    public final Double opr, dpr, ccwm;

    public StatsListElement(String key, String number, String name, String stat, Double opr, Double dpr, Double ccwm) {
        super(key);
        teamNumber = number;
        teamName = name;
        teamStat = stat;
        this.opr = opr;
        this.dpr = dpr;
        this.ccwm = ccwm;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_stats, null);

            holder = new ViewHolder();
            holder.teamNumber = (TextView) convertView.findViewById(R.id.team_number);
            holder.teamName = (TextView) convertView.findViewById(R.id.team_name);
            holder.teamStat = (TextView) convertView.findViewById(R.id.team_stat);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.teamNumber.setText("" + teamNumber);

        if (!teamName.isEmpty()) {
            holder.teamName.setText(teamName);
        } else {
            holder.teamName.setText("Team " + teamNumber);
        }

        holder.teamStat.setText(teamStat);

        return convertView;
    }

    private static class ViewHolder {
        TextView teamNumber;
        TextView teamName;
        TextView teamStat;
    }

    public int getTeamNumber() {
        return Integer.parseInt(teamNumber);
    }

    public Double getOpr() {
        return opr;
    }

    public String getFormattedOpr() {
        return ThreadSafeFormatters.formatDoubleTwoPlaces(opr);
    }

    public Double getDpr() {
        return dpr;
    }

    public String getFormattedDpr() {
        return ThreadSafeFormatters.formatDoubleTwoPlaces(dpr);
    }

    public Double getCcwm() {
        return ccwm;
    }

    public String getFormattedCcwm() {
        return ThreadSafeFormatters.formatDoubleTwoPlaces(ccwm);
    }

    public String getTeamNumberString() {
        return String.format("Team %1$s", teamNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StatsListElement)) {
            return false;
        }
        StatsListElement element = (StatsListElement) o;
        return teamName.equals(element.teamName)
          && teamNumber.equals(element.teamNumber)
          && opr.equals(element.opr)
          && dpr.equals(element.dpr)
          && ccwm.equals(element.ccwm);
    }
}
