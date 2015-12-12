package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;

/**
 * File created by phil on 6/4/14.
 */
public class AllianceListElement extends ListElement implements RenderableModel {

    public final int number;
    public final JsonArray teams;
    public final String eventKey;

    public AllianceListElement(String eventKey, int number, JsonArray teams) {
        if (teams.size() < 2) throw new IllegalArgumentException("Alliances have >= 2 members");
        this.number = number;
        this.teams = teams;
        this.eventKey = eventKey;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_alliance, null, false);

            holder = new ViewHolder();
            holder.allianceName = (TextView) convertView.findViewById(R.id.alliance_name);
            holder.memberOne = (TextView) convertView.findViewById(R.id.member_one);
            holder.memberTwo = (TextView) convertView.findViewById(R.id.member_two);
            holder.memberThree = (TextView) convertView.findViewById(R.id.member_three);
            holder.memberFour = (TextView) convertView.findViewById(R.id.member_four);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (convertView != null) {
            holder.allianceName.setText(String.format(c.getString(R.string.alliance_title), number));

            TeamAtEventClickListener listener = new TeamAtEventClickListener(c);

            String team1Key = teams.get(0).getAsString();
            SpannableString underLine = new SpannableString(team1Key.substring(3));
            underLine.setSpan(new UnderlineSpan(), 0, underLine.length(), 0);
            holder.memberOne.setText(underLine);
            holder.memberOne.setTag(team1Key + "@" + eventKey);
            holder.memberOne.setOnClickListener(listener);

            String team2Key = teams.get(1).getAsString();
            holder.memberTwo.setText(team2Key.substring(3));
            holder.memberTwo.setTag(team2Key + "@" + eventKey);
            holder.memberTwo.setOnClickListener(listener);

            if (teams.size() >= 3) {
                String team3Key = teams.get(2).getAsString();
                holder.memberThree.setText(team3Key.substring(3));
                holder.memberThree.setTag(team3Key + "@" + eventKey);
                holder.memberThree.setVisibility(View.VISIBLE);
                holder.memberThree.setOnClickListener(listener);
            }

            if (teams.size() >= 4) {
                String team4Key = teams.get(3).getAsString();
                holder.memberFour.setText(team4Key.substring(3));
                holder.memberFour.setTag(team4Key + "@" + eventKey);
                holder.memberFour.setVisibility(View.VISIBLE);
                holder.memberFour.setOnClickListener(listener);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView allianceName;
        TextView memberOne;
        TextView memberTwo;
        TextView memberThree;
        TextView memberFour;
    }

    @Override
    public ListElement render(ModelRendererSupplier supplier) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AllianceListElement)) {
            return false;
        }
        AllianceListElement other = (AllianceListElement) o;
        return number == other.number
                && teams.equals(other.teams)
                && eventKey.equals(other.eventKey);
    }
}
