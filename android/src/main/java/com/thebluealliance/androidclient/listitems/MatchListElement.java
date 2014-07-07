package com.thebluealliance.androidclient.listitems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.TeamClickListener;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement {

    private String videoKey;
    String matchTitle, redTeams[], blueTeams[], matchKey, redScore, blueScore;
    private String selectedTeamNumber;

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey) {
        super(matchKey);
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
        this.selectedTeamNumber = "";
    }

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey, String selectedTeamKey) {
        super(matchKey);
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
        this.selectedTeamNumber = selectedTeamKey.replace("frc", "");
    }

    @Override
    public View getView(final Context context, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = inflater.inflate(R.layout.list_item_match, null);

            holder = new ViewHolder();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.matchTitle.setTag(matchKey);

        if (!redScore.contains("?") && !blueScore.contains("?")) {
            try {
                int bScore = Integer.parseInt(blueScore),
                        rScore = Integer.parseInt(redScore);
                if (bScore > rScore) {
                    //blue wins
                    holder.blueAlliance.setBackgroundResource(R.drawable.blue_border);
                    holder.redAlliance.setBackgroundColor(context.getResources().getColor(R.color.lighter_red));
                } else if (bScore < rScore) {
                    //red wins
                    holder.redAlliance.setBackgroundResource(R.drawable.red_border);
                    holder.blueAlliance.setBackgroundColor(context.getResources().getColor(R.color.lighter_blue));
                }
            } catch (NumberFormatException e) {
                Log.w(Constants.LOG_TAG, "Attempted to parse an invalid match score.");
            }
        }

        //if we have video for this match, show an icon
        //currently the launcher icon. It'll be changed...
        if (videoKey != null) {
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.videoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoKey));
                    view.getContext().startActivity(intent);
                }
            });
        } else {
            holder.videoIcon.setVisibility(View.INVISIBLE);
        }

        holder.matchTitle.setText(matchTitle);

        TeamClickListener listener = new TeamClickListener(context);

        // Set team text depending on alliance size.
        if (redTeams.length == 0) {
            holder.red1.setText("");
            holder.red2.setText("");
            holder.red3.setText("");
        } else {
            holder.red1.setText(redTeams[0]);
            holder.red1.setTag("frc" + redTeams[0]);
            holder.red1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[0])) {
                holder.red1.setTypeface(Typeface.DEFAULT_BOLD);
            }

            holder.red2.setText(redTeams[1]);
            holder.red2.setTag("frc" + redTeams[1]);
            holder.red2.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[1])) {
                holder.red2.setTypeface(Typeface.DEFAULT_BOLD);
            }

            if (redTeams.length == 2) {
                holder.red3.setVisibility(View.GONE);
            } else {
                holder.red3.setVisibility(View.VISIBLE);
                holder.red3.setText(redTeams[2]);
                holder.red3.setTag("frc" + redTeams[2]);
                holder.red3.setOnClickListener(listener);
                if (selectedTeamNumber.equals(redTeams[2])) {
                    holder.red3.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        }

        if (blueTeams.length == 0) {
            holder.blue1.setText("");
            holder.blue2.setText("");
            holder.blue3.setText("");
        } else {
            holder.blue1.setText(blueTeams[0]);
            holder.blue1.setTag("frc" + blueTeams[0]);
            holder.blue1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(blueTeams[0])) {
                holder.blue1.setTypeface(Typeface.DEFAULT_BOLD);
            }

            holder.blue2.setText(blueTeams[1]);
            holder.blue2.setTag("frc" + blueTeams[1]);
            holder.blue2.setOnClickListener(listener);
            if (selectedTeamNumber.equals(blueTeams[1])) {
                holder.blue2.setTypeface(Typeface.DEFAULT_BOLD);
            }

            if (blueTeams.length == 2) {
                holder.blue3.setVisibility(View.GONE);
            } else {
                holder.blue3.setVisibility(View.VISIBLE);
                holder.blue3.setText(blueTeams[2]);
                holder.blue3.setTag("frc" + blueTeams[2]);
                holder.blue3.setOnClickListener(listener);
                if (selectedTeamNumber.equals(blueTeams[2])) {
                    holder.blue3.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        }
        holder.redScore.setText(redScore);
        holder.blueScore.setText(blueScore);

        return convertView;
    }

    private static class ViewHolder {
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
    }
}
