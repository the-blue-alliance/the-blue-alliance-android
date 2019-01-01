package com.thebluealliance.androidclient.views;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.TbaLogger;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listeners.EventTeamClickListener;
import com.thebluealliance.androidclient.listeners.MatchClickListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchView extends FrameLayout {

    @SuppressLint("SimpleDateFormat")
    private static java.text.DateFormat dowFormat = new SimpleDateFormat("E ");

    private TextView matchTitle, red1, red2, red3, blue1, blue2, blue3, redScore, blueScore, time;
    private View matchContainer, matchTitleContainer, columnHeadersContainer, teamsHeader,
            scoreHeader, timeHeader, redAlliance, blueAlliance, videoIcon, redDot1, redDot2,
            blueDot1, blueDot2, blueScoreContainer, redScoreContainer;

    private boolean showColumnHeaders, showScores, showTime;

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

        blueScoreContainer = findViewById(R.id.blue_score_container);
        redScoreContainer = findViewById(R.id.red_score_container);

        redDot1 = findViewById(R.id.red_dot_1);
        redDot2 = findViewById(R.id.red_dot_2);
        blueDot1 = findViewById(R.id.blue_dot_1);
        blueDot2 = findViewById(R.id.blue_dot_2);

        time = (TextView) findViewById(R.id.match_time);
    }

    public void initWithParams(String videoKey, String title, String[] redTeams, String[] blueTeams,
                               String redScore, String blueScore, String winner, String matchKey,
                               long time, String selectedTeamKey, boolean showVideoIcon,
                               int redExtraRp, int blueExtraRp) {

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
                if ("blue".equals(winner)) {
                    // blue wins
                    blueAlliance.setBackgroundResource(R.drawable.blue_border);
                    redAlliance.setBackgroundResource(R.drawable.no_border);
                } else if ("red".equals(winner)) {
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
                TbaLogger.w("Attempted to parse an invalid match score.");
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

        String eventKey = MatchHelper.getEventKeyFromMatchKey(matchKey);
        EventTeamClickListener listener = new EventTeamClickListener(getContext(), eventKey, null);

        // Set team text depending on alliance size.
        if (redTeams.length == 0) {
            red1.setText("");
            red2.setText("");
            red3.setText("");
        } else {
            red1.setVisibility(View.VISIBLE);
            red1.setText(redTeams[0]);
            red1.setTag(EventTeamHelper.generateKey(eventKey, "frc" + redTeams[0]));
            red1.setOnClickListener(listener);
            red1.setOnLongClickListener(listener);
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
            red2.setTag(EventTeamHelper.generateKey(eventKey, "frc" + redTeams[1]));
            red2.setOnClickListener(listener);
            red2.setOnLongClickListener(listener);
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
            red3.setTag(EventTeamHelper.generateKey(eventKey, "frc" + redTeams[2]));
            red3.setOnClickListener(listener);
            red3.setOnLongClickListener(listener);
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
            blue1.setTag(EventTeamHelper.generateKey(eventKey, "frc" + blueTeams[0]));
            blue1.setOnClickListener(listener);
            blue1.setOnLongClickListener(listener);
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
                blue2.setTag(EventTeamHelper.generateKey(eventKey, "frc" + blueTeams[1]));
                blue2.setOnClickListener(listener);
                blue2.setOnLongClickListener(listener);
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
                    blue3.setTag(EventTeamHelper.generateKey(eventKey, "frc" + blueTeams[2]));
                    blue3.setOnClickListener(listener);
                    blue3.setOnLongClickListener(listener);
                    if (selectedTeamNumber.equals(blueTeams[2])) {
                        blue3.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        blue3.setTypeface(Typeface.DEFAULT);
                    }
                }
            }
            this.redScore.setText(redScore);
            this.blueScore.setText(blueScore);

            if (redExtraRp > 0) {
                this.redDot1.setVisibility(View.VISIBLE);
            } else {
                this.redDot1.setVisibility(View.GONE);
            }
            if (redExtraRp > 1) {
                this.redDot2.setVisibility(View.VISIBLE);
            } else {
                this.redDot2.setVisibility(View.GONE);
            }

            if (blueExtraRp > 0) {
                this.blueDot1.setVisibility(View.VISIBLE);
            } else {
                this.blueDot1.setVisibility(View.GONE);
            }
            if (blueExtraRp > 1) {
                this.blueDot2.setVisibility(View.VISIBLE);
            } else {
                this.blueDot2.setVisibility(View.GONE);
            }

            String localTimeString;
            if (time <= 0) {
                // Match has no time
                localTimeString = getContext().getString(R.string.no_time_available);
            } else {
                // Format the day-of-week & time in the current locale with the user's 12/24-hour
                // preference. The day part distinguishes today's matches from tomorrow's matches
                // and from yesterday's matches with delayed results.
                Date date = new Date(time * 1000L);
                java.text.DateFormat format = DateFormat.getTimeFormat(getContext());
                localTimeString = dowFormat.format(date) + format.format(date);
            }

            this.time.setText(localTimeString);
        }
    }

    /**
     * Sets if the whole view should be clickable or not. If so, the view will show touch feedback
     * and open match details in a new activity when its clicked. Otherwise, nothing happens.
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
            redScoreContainer.setVisibility(VISIBLE);
            blueScoreContainer.setVisibility(VISIBLE);
            scoreHeader.setVisibility(showColumnHeaders ? VISIBLE : GONE);

        } else {
            redScore.setVisibility(GONE);
            blueScore.setVisibility(GONE);
            redScoreContainer.setVisibility(GONE);
            blueScoreContainer.setVisibility(GONE);
            scoreHeader.setVisibility(GONE);
        }
    }
}
