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
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;
import com.thebluealliance.androidclient.models.Team;

import java.util.Map;

public class CardedAwardListElement extends ListElement {

    public final String awardName;
    public final String eventKey;
    public final String selectedTeamNum;
    public final JsonArray awardWinners;
    private final Map<String, Team> mAwardTeams;

    public CardedAwardListElement(String name, JsonArray winners) {
        super();
        awardName = name;
        awardWinners = winners;
        mAwardTeams = null;
        eventKey = "";
        selectedTeamNum = "";
    }

    public CardedAwardListElement(String name, String eventKey, JsonArray winners, Map<String, Team> teams, String selectedTeamKey) {
        super();
        this.awardName = name;
        this.eventKey = eventKey;
        this.awardWinners = winners;
        this.mAwardTeams = teams;
        this.selectedTeamNum = (selectedTeamKey == null || selectedTeamKey.length() < 4)
          ? "" 
          : selectedTeamKey.substring(3);
    }

    @Override
    public View getView(Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_carded_award, null);

            holder = new ViewHolder();
            holder.awardName = (TextView) convertView.findViewById(R.id.award_name);
            holder.awardRecipients = (LinearLayout) convertView.findViewById(R.id.award_recipients);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.awardRecipients.removeAllViews();
        }

        holder.awardName.setText(awardName);

        for (JsonElement mAwardWinner : awardWinners) {
            JsonObject winner = mAwardWinner.getAsJsonObject();
            View winnerView = inflater.inflate(R.layout.list_item_award_recipient, null);

            String teamNumber;
            String awardee;
            if (JSONHelper.isNull(winner.get("team_number"))) {
                teamNumber = "";
            } else {
                teamNumber = winner.get("team_number").getAsString();
                if (!selectedTeamNum.equals(teamNumber)) {
                    winnerView.setOnClickListener(new TeamAtEventClickListener(context));
                } else {
                    winnerView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));  // disable touch feedback
                }
                winnerView.setTag("frc" + teamNumber + "@" + eventKey);
            }
            if (JSONHelper.isNull(winner.get("awardee"))) {
                awardee = "";
            } else {
                awardee = winner.get("awardee").getAsString();
            }

            String awardLine1 = "";
            String awardLine2 = "";

            if (teamNumber.isEmpty()) {
                if (!awardee.isEmpty()) {
                    awardLine1 = awardee;
                }
            } else {
                Team team;
                if (mAwardTeams == null) {
                    team = DataManager.Teams.getTeamFromDB(context, "frc" + teamNumber);
                } else {
                    team = mAwardTeams.get("frc" + teamNumber);
                }

                String nickname;
                if (team == null) {
                    nickname = "Team " + teamNumber;
                } else {
                    nickname = team.getNickname();
                }

                if (awardee.isEmpty() && nickname.isEmpty()) {
                    awardLine1 = teamNumber;
                    awardLine2 = "Team " + teamNumber;
                } else if (awardee.isEmpty()) {
                    awardLine1 = teamNumber;
                    awardLine2 = nickname;
                } else {
                    awardLine1 = awardee;
                    awardLine2 = nickname;
                }
            }

            TextView winnerLine1 = (TextView) winnerView.findViewById(R.id.winner_line_1);
            winnerLine1.setText(awardLine1);

            TextView winnerLine2 = (TextView) winnerView.findViewById(R.id.winner_line_2);
            if (awardLine2.isEmpty()) {
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
