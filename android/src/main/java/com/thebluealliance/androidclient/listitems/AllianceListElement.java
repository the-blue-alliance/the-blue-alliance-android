package com.thebluealliance.androidclient.listitems;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.interfaces.RenderableModel;
import com.thebluealliance.androidclient.listeners.EventTeamClickListener;
import com.thebluealliance.androidclient.renderers.ModelRendererSupplier;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class AllianceListElement extends ListElement implements RenderableModel {

    public final int number;
    public final PlayoffAdvancement advancement;
    public final JsonArray teams;
    public final String eventKey;

    public AllianceListElement(String eventKey, int number, JsonArray teams, PlayoffAdvancement advancement) {
        if (teams.size() < 2) throw new IllegalArgumentException("Alliances have >= 2 members");
        this.number = number;
        this.advancement = advancement;
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
            holder.advancement = (TextView) convertView.findViewById(R.id.alliance_advancement);
            holder.memberOne = (TextView) convertView.findViewById(R.id.member_one);
            holder.memberTwo = (TextView) convertView.findViewById(R.id.member_two);
            holder.memberThree = (TextView) convertView.findViewById(R.id.member_three);
            holder.memberFour = (TextView) convertView.findViewById(R.id.member_four);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.allianceName.setText(String.format(c.getString(R.string.alliance_title), number));

        if (advancement != PlayoffAdvancement.NONE) {
            holder.advancement.setVisibility(View.VISIBLE);
            holder.advancement.setText(advancement.getAbbreviation());
        } else {
            holder.advancement.setVisibility(View.VISIBLE);
            holder.advancement.setText("");
        }

        EventTeamClickListener listener = new EventTeamClickListener(c);

        String team1Key = teams.get(0).getAsString();
        SpannableString underLine = new SpannableString(team1Key.substring(3));
        underLine.setSpan(new UnderlineSpan(), 0, underLine.length(), 0);
        holder.memberOne.setText(underLine);
        holder.memberOne.setTag(EventTeamHelper.generateKey(eventKey, team1Key));
        holder.memberOne.setOnClickListener(listener);
        holder.memberOne.setOnLongClickListener(listener);

        String team2Key = teams.get(1).getAsString();
        holder.memberTwo.setText(team2Key.substring(3));
        holder.memberTwo.setTag(EventTeamHelper.generateKey(eventKey, team2Key));
        holder.memberTwo.setOnClickListener(listener);
        holder.memberTwo.setOnLongClickListener(listener);

        if (teams.size() >= 3) {
            String team3Key = teams.get(2).getAsString();
            holder.memberThree.setText(team3Key.substring(3));
            holder.memberThree.setTag(EventTeamHelper.generateKey(eventKey, team3Key));
            holder.memberThree.setVisibility(View.VISIBLE);
            holder.memberThree.setOnClickListener(listener);
            holder.memberThree.setOnLongClickListener(listener);
        }

        if (teams.size() >= 4) {
            String team4Key = teams.get(3).getAsString();
            holder.memberFour.setText(team4Key.substring(3));
            holder.memberFour.setTag(EventTeamHelper.generateKey(eventKey, team4Key));
            holder.memberFour.setVisibility(View.VISIBLE);
            holder.memberFour.setOnClickListener(listener);
            holder.memberFour.setOnLongClickListener(listener);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView allianceName;
        TextView advancement;
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
