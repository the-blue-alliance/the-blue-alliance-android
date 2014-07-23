package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.MatchClickListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;

import java.io.Serializable;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement implements Serializable{

    private String videoKey, matchTitle, redTeams[], blueTeams[], matchKey, redScore, blueScore, selectedTeamNumber;
    private boolean showVideoIcon, showMatchHeader;

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey, String selectedTeamKey, boolean showVideoIcon, boolean showHeader) {
        super(matchKey);
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
        if(selectedTeamKey != null && !selectedTeamKey.isEmpty()) {
            this.selectedTeamNumber = selectedTeamKey.replace("frc", "");
        }else{
            this.selectedTeamNumber = "";
        }
        this.showVideoIcon = showVideoIcon;
        this.showMatchHeader = showHeader;
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_match, null);

            holder = new ViewHolder();
            holder.matchContainer = (LinearLayout) convertView.findViewById(R.id.match_container);
            holder.matchTitleContainer = (RelativeLayout) convertView.findViewById(R.id.match_title_container);
            holder.matchTitle = (TextView) convertView.findViewById(R.id.match_title);
            holder.red1 = (TextView) convertView.findViewById(R.id.red1);
            holder.red2 = (TextView) convertView.findViewById(R.id.red2);
            holder.red3 = (TextView) convertView.findViewById(R.id.red3);
            holder.blue1 = (TextView) convertView.findViewById(R.id.blue1);
            holder.blue2 = (TextView) convertView.findViewById(R.id.blue2);
            holder.blue3 = (TextView) convertView.findViewById(R.id.blue3);
            holder.redScore = (TextView) convertView.findViewById(R.id.red_score);
            holder.blueScore = (TextView) convertView.findViewById(R.id.blue_score);
            holder.redAlliance = convertView.findViewById(R.id.red_alliance);
            holder.blueAlliance = convertView.findViewById(R.id.blue_alliance);
            holder.videoIcon = (ImageView) convertView.findViewById(R.id.match_video);
            holder.header = (TableRow) convertView.findViewById(R.id.match_header);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(showMatchHeader) {
           holder.matchContainer.setClickable(false);
           holder.matchContainer.setBackgroundResource(R.drawable.transparent);
        }else{
            holder.matchContainer.setOnClickListener(new MatchClickListener(context));
        }

        holder.matchTitle.setTag(matchKey);
        holder.red1.setLines(1);  // To prevent layout issues when ListView recycles items

        if (!redScore.contains("?") && !blueScore.contains("?")) {
            try {
                int bScore = Integer.parseInt(blueScore),
                        rScore = Integer.parseInt(redScore);
                if (bScore > rScore) {
                    //blue wins
                    holder.blueAlliance.setBackgroundResource(R.drawable.blue_border);
                    holder.redAlliance.setBackgroundResource(R.drawable.no_border);
                } else if (bScore < rScore) {
                    //red wins
                    holder.redAlliance.setBackgroundResource(R.drawable.red_border);
                    holder.blueAlliance.setBackgroundResource(R.drawable.no_border);
                }
                else {
                    // tie
                    holder.redAlliance.setBackgroundResource(R.drawable.no_border);
                    holder.blueAlliance.setBackgroundResource(R.drawable.no_border);
                }
            } catch (NumberFormatException e) {
                holder.redAlliance.setBackgroundResource(R.drawable.no_border);
                holder.blueAlliance.setBackgroundResource(R.drawable.no_border);
                Log.w(Constants.LOG_TAG, "Attempted to parse an invalid match score.");
            }
        }
        // Match hasn't been played yet. Don't border anything.
        else {
            holder.redAlliance.setBackgroundResource(R.drawable.no_border);
            holder.blueAlliance.setBackgroundResource(R.drawable.no_border);
        }

        //if we have video for this match, show an icon
        if (videoKey != null && showVideoIcon) {
            holder.videoIcon.setVisibility(View.VISIBLE);
        } else {
            holder.videoIcon.setVisibility(View.GONE);
        }

        if (showMatchHeader){
            holder.header.setVisibility(View.VISIBLE);
            holder.matchTitleContainer.setVisibility(View.GONE);
        } else {
            holder.header.setVisibility(View.GONE);
            holder.matchTitleContainer.setVisibility(View.VISIBLE);
        }

        holder.matchTitle.setText(matchTitle);

        TeamAtEventClickListener listener = new TeamAtEventClickListener(context);
        String eventKey = matchKey.split("_")[0];

        // Set team text depending on alliance size.
        if (redTeams.length == 0) {
            holder.red1.setText("");
            holder.red2.setText("");
            holder.red3.setText("");
        } else {
            holder.red1.setVisibility(View.VISIBLE);
            holder.red1.setText(redTeams[0]);
            holder.red1.setTag("frc" + redTeams[0] + "@" + eventKey);
            holder.red1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[0])) {
                holder.red1.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.red1.setTypeface(Typeface.DEFAULT);
            }

        }

        if (redTeams.length == 1) {
            holder.red2.setVisibility(View.GONE);
            holder.red3.setVisibility(View.GONE);
        } else {
            holder.red2.setVisibility(View.VISIBLE);
            holder.red2.setText(redTeams[1]);
            holder.red2.setTag("frc" + redTeams[1] + "@" + eventKey);
            holder.red2.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[1])) {
                holder.red2.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.red2.setTypeface(Typeface.DEFAULT);
            }
        }
        if (redTeams.length == 2) {
            holder.red3.setVisibility(View.GONE);
        } else {
            holder.red3.setVisibility(View.VISIBLE);
            holder.red3.setText(redTeams[2]);
            holder.red3.setTag("frc" + redTeams[2] + "@" + eventKey);
            holder.red3.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[2])) {
                holder.red3.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.red3.setTypeface(Typeface.DEFAULT);
            }
        }

        if (blueTeams.length == 0) {
            holder.blue1.setText("");
            holder.blue2.setText("");
            holder.blue3.setText("");
        } else {
            holder.blue1.setVisibility(View.VISIBLE);
            holder.blue1.setText(blueTeams[0]);
            holder.blue1.setTag("frc" + blueTeams[0] + "@" + eventKey);
            holder.blue1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(blueTeams[0])) {
                holder.blue1.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.blue1.setTypeface(Typeface.DEFAULT);
            }

            if (blueTeams.length == 1) {
                holder.blue2.setVisibility(View.GONE);
                holder.blue3.setVisibility(View.GONE);
            } else {
                holder.blue2.setVisibility(View.VISIBLE);
                holder.blue2.setText(blueTeams[1]);
                holder.blue2.setTag("frc" + blueTeams[1] + "@" + eventKey);
                holder.blue2.setOnClickListener(listener);
                if (selectedTeamNumber.equals(blueTeams[1])) {
                    holder.blue2.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    holder.blue2.setTypeface(Typeface.DEFAULT);
                }

                if (blueTeams.length == 2) {
                    holder.blue3.setVisibility(View.GONE);
                } else {
                    holder.blue3.setVisibility(View.VISIBLE);
                    holder.blue3.setText(blueTeams[2]);
                    holder.blue3.setTag("frc" + blueTeams[2] + "@" + eventKey);
                    holder.blue3.setOnClickListener(listener);
                    if (selectedTeamNumber.equals(blueTeams[2])) {
                        holder.blue3.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        holder.blue3.setTypeface(Typeface.DEFAULT);
                    }
                }
            }
            holder.redScore.setText(redScore);
            holder.blueScore.setText(blueScore);
        }
        return convertView;
    }

        private static class ViewHolder {
            LinearLayout matchContainer;
            RelativeLayout matchTitleContainer;
            TextView matchTitle;
            TextView red1;
            TextView red2;
            TextView red3;
            TextView blue1;
            TextView blue2;
            TextView blue3;
            TextView redScore;
            TextView blueScore;
            View redAlliance;
            View blueAlliance;
            ImageView videoIcon;
            TableRow header;
        }
    }
