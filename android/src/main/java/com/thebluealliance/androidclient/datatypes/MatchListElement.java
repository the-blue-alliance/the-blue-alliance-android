package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.TeamClickListener;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement {

    private String videoKey;
    String matchTitle, redTeams[], blueTeams[], matchKey, redScore, blueScore;

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey) {
        super();
        this.videoKey = youTubeVideoKey;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        ViewHolder holder;
        if (convertView == null) {
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
            holder.videoIcon = (ImageView) convertView.findViewById(R.id.match_video);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //if we have video for this match, show an icon
        //currently the launcher icon. It'll be changed...
        if (videoKey != null) {
            holder.videoIcon.setVisibility(View.VISIBLE);
            holder.videoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
                    view.getContext().startActivity(intent);
                }
            });
        } else {
            holder.videoIcon.setVisibility(View.INVISIBLE);
        }

        holder.matchTitle.setText(matchTitle);

        TeamClickListener listener = new TeamClickListener(c);

        // Set team text depending on alliance size.
        if (redTeams.length == 0) {
            holder.red1.setText("");
            holder.red2.setText("");
            holder.red3.setText("");
        } else {
            holder.red1.setText(redTeams[0]);
            holder.red1.setTag("frc" + redTeams[0]);
            holder.red1.setOnClickListener(listener);

            holder.red2.setText(redTeams[1]);
            holder.red2.setTag("frc" + redTeams[1]);
            holder.red2.setOnClickListener(listener);

            if (redTeams.length == 2) {
                holder.red3.setVisibility(View.GONE);
            } else {
                holder.red3.setVisibility(View.VISIBLE);
                holder.red3.setText(redTeams[2]);
                holder.red3.setTag("frc" + redTeams[2]);
                holder.red3.setOnClickListener(listener);
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

            holder.blue2.setText(blueTeams[1]);
            holder.blue2.setTag("frc" + blueTeams[1]);
            holder.blue2.setOnClickListener(listener);

            if (blueTeams.length == 2) {
                holder.blue3.setVisibility(View.GONE);
            } else {
                holder.blue3.setVisibility(View.VISIBLE);
                holder.blue3.setText(blueTeams[2]);
                holder.blue3.setTag("frc"+blueTeams[2]);
                holder.blue3.setOnClickListener(listener);
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
        ImageView videoIcon;
    }
}
