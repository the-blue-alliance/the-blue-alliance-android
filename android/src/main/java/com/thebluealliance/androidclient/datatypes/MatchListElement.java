package com.thebluealliance.androidclient.datatypes;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebluealliance.androidclient.R;

/**
 * File created by phil on 4/20/14.
 */
public class MatchListElement extends ListElement {

    private boolean video;
    String matchTitle, redTeams[],blueTeams[], matchKey;
    int redScore, blueScore;

    public MatchListElement(boolean video, String matchTitle, String[] redTeams, String[] blueTeams, int redScore, int blueScore, String matchKey) {
        super();
        this.video = video;
        this.matchTitle = matchTitle;
        this.redTeams = redTeams;
        this.blueTeams = blueTeams;
        this.redScore = redScore;
        this.blueScore = blueScore;
        this.matchKey = matchKey;
    }

    @Override
    public View getView(LayoutInflater inflater, View view) {
        if(view == null){
            view = inflater.inflate(R.layout.match_list_item, null);
            ImageView videoIcon = (ImageView)view.findViewById(R.id.match_video);

            //if we have video for this match, show an icon
            //currently the launcher icon. It'll be changed...
            if(video) videoIcon.setBackgroundResource(R.drawable.ic_action_play_over_video);

            TextView matchTitle = (TextView)view.findViewById(R.id.match_title),
                     red1       = (TextView)view.findViewById(R.id.red1),
                     red2       = (TextView)view.findViewById(R.id.red2),
                     red3       = (TextView)view.findViewById(R.id.red3),
                     blue1      = (TextView)view.findViewById(R.id.blue1),
                     blue2      = (TextView)view.findViewById(R.id.blue2),
                     blue3      = (TextView)view.findViewById(R.id.blue3),
                     red_score  = (TextView)view.findViewById(R.id.red_score),
                     blue_score = (TextView)view.findViewById(R.id.blue_score);

            matchTitle.setText(this.matchTitle);
            red1.setText(redTeams[0]);
            red2.setText(redTeams[1]);
            red3.setText(redTeams[2]);
            blue1.setText(blueTeams[0]);
            blue2.setText(blueTeams[1]);
            blue3.setText(blueTeams[2]);
            red_score.setText(Integer.toString(redScore));
            blue_score.setText(Integer.toString(blueScore));
        }
        return view;
    }
}
