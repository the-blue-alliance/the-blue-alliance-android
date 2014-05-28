package com.thebluealliance.androidclient.datatypes;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement {

    private String videoKey;
    String matchTitle, redTeams[], blueTeams[], matchKey;
    int redScore, blueScore;
    private ViewHolder holder;

    public MatchListElement(String youTubeVideoKey, String matchTitle, String[] redTeams, String[] blueTeams, int redScore, int blueScore, String matchKey) {
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
        if (convertView == null || holder == null) {
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

            Resources resources = c.getResources();
            if(blueScore> redScore){
                //blue wins
                View blue_alliance = convertView.findViewById(R.id.blue_alliance);
                if(blue_alliance != null) {
                    blue_alliance.setBackgroundDrawable(resources.getDrawable(R.drawable.blue_border));
                }
                convertView.findViewById(R.id.blue_score).setBackgroundDrawable(resources.getDrawable(R.drawable.blue_score_border));
            }else if(blueScore < redScore){
                //red wins
                View red_alliance = convertView.findViewById(R.id.red_alliance);
                if(red_alliance != null) {
                    red_alliance.setBackgroundDrawable(resources.getDrawable(R.drawable.red_border));
                }
                convertView.findViewById(R.id.red_score).setBackgroundDrawable(resources.getDrawable(R.drawable.red_score_border));
            }
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
        holder.red1.setText(redTeams[0]);
        holder.red2.setText(redTeams[1]);
        holder.red3.setText(redTeams[2]);
        holder.blue1.setText(blueTeams[0]);
        holder.blue2.setText(blueTeams[1]);
        holder.blue3.setText(blueTeams[2]);
        holder.redScore.setText(Integer.toString(redScore));
        holder.blueScore.setText(Integer.toString(blueScore));
        return convertView;
    }

    private class ViewHolder {
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
