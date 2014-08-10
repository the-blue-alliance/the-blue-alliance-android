package com.thebluealliance.androidclient.views;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.listeners.MatchClickListener;
import com.thebluealliance.androidclient.listeners.TeamAtEventClickListener;

import java.util.Date;

/**
 * Created by Nathan on 8/3/2014.
 */
public class MatchView extends FrameLayout {

    TextView matchTitle, red1, red2, red3, blue1, blue2, blue3, redScore, blueScore, time;
    View matchContainer, matchTitleContainer, columnHeadersContainer, teamsHeader, scoreHeader, timeHeader, redAlliance, blueAlliance, videoIcon;

    boolean showColumnHeaders, showScores, showTime;

    public MatchView(Context context) {
        super(context);
        init();
    }

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MatchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Inflate the layout
        LayoutInflater.from(getContext()).inflate(R.layout.list_item_match, this, true);

        // Grab references to child views to avoid repeated calls to findViewById
        matchContainer = findViewById(R.id.match_container);

        matchTitleContainer = findViewById(R.id.match_title_container);
        matchTitle = (TextView) findViewById(R.id.match_title);

        red1 = (TextView) findViewById(R.id.red1);
        red2 = (TextView) findViewById(R.id.red2);
        red3 = (TextView) findViewById(R.id.red3);
        redScore = (TextView) findViewById(R.id.red_score);

        blue1 = (TextView) findViewById(R.id.blue1);
        blue2 = (TextView) findViewById(R.id.blue2);
        blue3 = (TextView) findViewById(R.id.blue3);
        blueScore = (TextView) findViewById(R.id.blue_score);

        columnHeadersContainer = findViewById(R.id.column_headers_container);
        teamsHeader = findViewById(R.id.teams_header);
        scoreHeader = findViewById(R.id.score_header);
        timeHeader = findViewById(R.id.time_header);

        redAlliance = findViewById(R.id.red_alliance);
        blueAlliance = findViewById(R.id.blue_alliance);

        videoIcon = findViewById(R.id.match_video);

        time = (TextView) findViewById(R.id.match_time);
    }

    public void initWithParams(String videoKey, String title, String[] redTeams, String[] blueTeams, String redScore, String blueScore, String matchKey, long time, String selectedTeamKey, boolean showVideoIcon) {

        // Parse selected team key for a number
        String selectedTeamNumber;
        if (selectedTeamKey != null && !selectedTeamKey.isEmpty()) {
            selectedTeamNumber = selectedTeamKey.replace("frc", "");
        } else {
            selectedTeamNumber = "";
        }

        matchTitle.setTag(matchKey);
        red1.setLines(1);  // To prevent layout issues when ListView recycles items

        if (!redScore.contains("?") && !blueScore.contains("?")) {
            try {
                int bScore = Integer.parseInt(blueScore),
                        rScore = Integer.parseInt(redScore);
                if (bScore > rScore) {
                    // blue wins
                    blueAlliance.setBackgroundResource(R.drawable.blue_border);
                    redAlliance.setBackgroundResource(R.drawable.no_border);
                } else if (bScore < rScore) {
                    // red wins
                    redAlliance.setBackgroundResource(R.drawable.red_border);
                    blueAlliance.setBackgroundResource(R.drawable.no_border);
                } else {
                    // tie
                    redAlliance.setBackgroundResource(R.drawable.no_border);
                    blueAlliance.setBackgroundResource(R.drawable.no_border);
                }
            } catch (NumberFormatException e) {
                redAlliance.setBackgroundResource(R.drawable.no_border);
                blueAlliance.setBackgroundResource(R.drawable.no_border);
                Log.w(Constants.LOG_TAG, "Attempted to parse an invalid match score.");
            }
        }
        // Match hasn't been played yet. Don't border anything.
        else {
            redAlliance.setBackgroundResource(R.drawable.no_border);
            blueAlliance.setBackgroundResource(R.drawable.no_border);
        }

        // If we have video for this match, show an icon
        if (videoKey != null && showVideoIcon) {
            videoIcon.setVisibility(View.VISIBLE);
        } else {
            videoIcon.setVisibility(View.GONE);
        }

        matchTitle.setText(title);

        TeamAtEventClickListener listener = new TeamAtEventClickListener(getContext());
        String eventKey = matchKey.split("_")[0];

        // Set team text depending on alliance size.
        if (redTeams.length == 0) {
            red1.setText("");
            red2.setText("");
            red3.setText("");
        } else {
            red1.setVisibility(View.VISIBLE);
            red1.setText(redTeams[0]);
            red1.setTag("frc" + redTeams[0] + "@" + eventKey);
            red1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[0])) {
                red1.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                red1.setTypeface(Typeface.DEFAULT);
            }
        }

        if (redTeams.length == 1) {
            red2.setVisibility(View.GONE);
            red3.setVisibility(View.GONE);
        } else {
            red2.setVisibility(View.VISIBLE);
            red2.setText(redTeams[1]);
            red2.setTag("frc" + redTeams[1] + "@" + eventKey);
            red2.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[1])) {
                red2.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                red2.setTypeface(Typeface.DEFAULT);
            }
        }

        if (redTeams.length == 2) {
            red3.setVisibility(View.GONE);
        } else {
            red3.setVisibility(View.VISIBLE);
            red3.setText(redTeams[2]);
            red3.setTag("frc" + redTeams[2] + "@" + eventKey);
            red3.setOnClickListener(listener);
            if (selectedTeamNumber.equals(redTeams[2])) {
                red3.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                red3.setTypeface(Typeface.DEFAULT);
            }
        }

        if (blueTeams.length == 0) {
            blue1.setText("");
            blue2.setText("");
            blue3.setText("");
        } else {
            blue1.setVisibility(View.VISIBLE);
            blue1.setText(blueTeams[0]);
            blue1.setTag("frc" + blueTeams[0] + "@" + eventKey);
            blue1.setOnClickListener(listener);
            if (selectedTeamNumber.equals(blueTeams[0])) {
                blue1.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                blue1.setTypeface(Typeface.DEFAULT);
            }

            if (blueTeams.length == 1) {
                blue2.setVisibility(View.GONE);
                blue3.setVisibility(View.GONE);
            } else {
                blue2.setVisibility(View.VISIBLE);
                blue2.setText(blueTeams[1]);
                blue2.setTag("frc" + blueTeams[1] + "@" + eventKey);
                blue2.setOnClickListener(listener);
                if (selectedTeamNumber.equals(blueTeams[1])) {
                    blue2.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    blue2.setTypeface(Typeface.DEFAULT);
                }

                if (blueTeams.length == 2) {
                    blue3.setVisibility(View.GONE);
                } else {
                    blue3.setVisibility(View.VISIBLE);
                    blue3.setText(blueTeams[2]);
                    blue3.setTag("frc" + blueTeams[2] + "@" + eventKey);
                    blue3.setOnClickListener(listener);
                    if (selectedTeamNumber.equals(blueTeams[2])) {
                        blue3.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        blue3.setTypeface(Typeface.DEFAULT);
                    }
                }
            }
            this.redScore.setText(redScore);
            this.blueScore.setText(blueScore);

            String localTimeString = "";
            if (time <= 0) {
                // Match has no time
                localTimeString = getContext().getString(R.string.no_time_available);
            } else {
                Date date = new Date(time * 1000L);
                java.text.DateFormat format = DateFormat.getTimeFormat(getContext());
                localTimeString = format.format(date);
            }

            this.time.setText(localTimeString);
        }
    }

    /**
     * Sets if the whole view should be clickable or not. If so, the view will show touch feedback and
     * open match details in a new activity when its clicked. Otherwise, nothing happens.
     *
     * @param clickable true if the view should be clickable, false if otherwise
     */
    public void setClickToShowDetails(boolean clickable) {
        if (clickable) {
            matchContainer.setOnClickListener(new MatchClickListener(getContext()));
        } else {
            matchContainer.setClickable(false);
            matchContainer.setBackgroundResource(R.drawable.transparent);
        }
    }

    public void showMatchTitle(boolean show) {
        matchTitleContainer.setVisibility(show ? VISIBLE : GONE);
    }

    public void showColumnHeaders(boolean show) {
        showColumnHeaders = show;
        if (showColumnHeaders) {
            columnHeadersContainer.setVisibility(View.VISIBLE);
            scoreHeader.setVisibility(showScores ? VISIBLE : GONE);
            timeHeader.setVisibility(showTime ? VISIBLE : GONE);
        } else {
            columnHeadersContainer.setVisibility(GONE);
        }
    }

    public void showTime(boolean show) {
        showTime = show;
        if (showTime) {
            time.setVisibility(VISIBLE);
            timeHeader.setVisibility(showColumnHeaders ? VISIBLE : GONE);
        } else {
            time.setVisibility(GONE);
            timeHeader.setVisibility(GONE);
        }
    }

    public void showScores(boolean show) {
        showScores = show;
        if (showScores) {
            redScore.setVisibility(VISIBLE);
            blueScore.setVisibility(VISIBLE);
            scoreHeader.setVisibility(showColumnHeaders ? VISIBLE : GONE);

        } else {
            redScore.setVisibility(GONE);
            blueScore.setVisibility(GONE);
            scoreHeader.setVisibility(GONE);
        }
    }
}
