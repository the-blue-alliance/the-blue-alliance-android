package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.listeners.TeamClickListener;
import com.thebluealliance.androidclient.models.Team;

import java.util.Iterator;

/**
 * File created by phil on 4/23/14.
 */
public class AwardListElement extends ListElement {

    private String mAwardName;
    private JsonArray mAwardWinners;

    public AwardListElement(String name, JsonArray winners) {
        super();
        mAwardName = name;
        mAwardWinners = winners;
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_award, null);

            holder = new ViewHolder();
            holder.awardName = (TextView) convertView.findViewById(R.id.award_name);
            holder.awardRecipients = (LinearLayout) convertView.findViewById(R.id.award_recipients);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.awardRecipients.removeAllViews();
        }

        holder.awardName.setText(mAwardName);

        Iterator<JsonElement> iterator = mAwardWinners.iterator();
        while (iterator.hasNext()) {
            JsonObject winner = iterator.next().getAsJsonObject();
            View winnerView = inflater.inflate(R.layout.list_item_award_recipient, null);

            String teamNumber = "";
            String awardee = "";
            if (winner.get("team_number").isJsonNull()) {
                teamNumber = "";
            } else {
                teamNumber = winner.get("team_number").getAsString();
                winnerView.setOnClickListener(new TeamClickListener(context));
                winnerView.setTag("frc" + teamNumber);
            }
            if (winner.get("awardee").isJsonNull()) {
                awardee = "";
            } else {
                awardee = winner.get("awardee").getAsString();
            }

            String awardLine1 = "";
            String awardLine2 = "";

            if (teamNumber == "") {
                if (awardee != "") {
                    awardLine1 = awardee;
                }
            } else {
                Team team = new Team();
                team.setTeamNumber(111);
                team.setNickname("WildStang");
                //Team team = DataManager.Teams.getTeamFromDB(context, "frc" + teamNumber);
                if (awardee == "") {
                    awardLine1 = teamNumber;
                    awardLine2 = team.getNickname();
                } else {
                    awardLine1 = awardee;
                    awardLine2 = teamNumber + " " + team.getNickname();
                }
            }

            TextView winnerLine1 = (TextView) winnerView.findViewById(R.id.winner_line_1);
            winnerLine1.setText(awardLine1);

            TextView winnerLine2 = (TextView) winnerView.findViewById(R.id.winner_line_2);
            if (awardLine2 == "") {
                winnerLine2.setVisibility(View.GONE);
            } else {
                winnerLine2.setText(awardLine2);
            }

            holder.awardRecipients.addView(winnerView);
        }

        return convertView;
    }
    private static class ViewHolder {
        TextView awardName;
        LinearLayout awardRecipients;
    }
}