package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.comparators.MatchSortByDisplayOrderComparator;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.EventMatchesEvent;
import com.thebluealliance.androidclient.eventbus.LiveEventMatchUpdateEvent;
import com.thebluealliance.androidclient.firebase.AllianceAdvancementEvent;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.types.MatchType;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MatchListSubscriber extends BaseAPISubscriber<List<Match>, List<ListGroup>> {

    private ListGroup mQualMatches;
    private ListGroup mOctoMatches;
    private ListGroup mQuarterMatches;
    private ListGroup mSemiMatches;
    private ListGroup mFinalMatches;
    private String mTeamKey;
    private String mEventKey;
    private Database mDb;
    private EventBus mEventBus;
    private HashMap<String, PlayoffAdvancement> mAdvancement;

    public MatchListSubscriber(Resources resources, Database db, EventBus eventBus) {
        super();
        mDataToBind = new ArrayList<>();
        mQualMatches = new ListGroup(resources.getString(R.string.quals_header));
        mOctoMatches = new ListGroup(resources.getString(R.string.octo_header));
        mQuarterMatches = new ListGroup(resources.getString(R.string.quarters_header));
        mSemiMatches = new ListGroup(resources.getString(R.string.semis_header));
        mFinalMatches = new ListGroup(resources.getString(R.string.finals_header));
        mAdvancement = new HashMap<>();
        mDb = db;
        mEventBus = eventBus;
        mTeamKey = null;
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        mQualMatches.clear();
        mOctoMatches.clear();
        mQuarterMatches.clear();
        mSemiMatches.clear();
        mFinalMatches.clear();

        int[] record = {0, 0, 0}; //wins, losses, ties
        Match nextMatch = null;
        Match lastMatch = null;

        Event event = mDb.getEventsTable().get(mEventKey);
        if (event != null && event.isHappeningNow()) {
            Collections.sort(mAPIData, new MatchSortByPlayOrderComparator());
        } else {
            Collections.sort(mAPIData, new MatchSortByDisplayOrderComparator());
        }

        ListGroup currentGroup = mQualMatches;
        MatchType lastType = null;
        Match previousIteration = null;
        boolean lastMatchPlayed = false;
        int redFinalsWon = 0;
        int blueFinalsWon = 0;
        if (mAPIData.size() > 0) {
            nextMatch = mAPIData.get(0);
        }
        for (int i = 0; i < mAPIData.size(); i++) {
            Match match = mAPIData.get(i);
            MatchType currentType = match.getMatchType();
            JsonObject alliances = match.getAlliances();
            if (lastType != currentType) {
                switch (match.getMatchType()) {
                    case QUAL:
                        currentGroup = mQualMatches;
                        break;
                    case OCTO:
                        currentGroup = mOctoMatches;
                        break;
                    case QUARTER:
                        currentGroup = mQuarterMatches;
                        break;
                    case SEMI:
                        currentGroup = mSemiMatches;
                        break;
                    case FINAL:
                        currentGroup = mFinalMatches;
                        break;
                }
            }

            currentGroup.children.add(match);

            if (lastMatchPlayed && !match.hasBeenPlayed()) {
                lastMatch = previousIteration;
                nextMatch = match;
            }

            /* Track alliance advancement, indexed by captain team key */
            if (match.getMatchType() == MatchType.FINAL && match.hasBeenPlayed()) {
                // Need to ensure we can differentiate who won the finals
                if (Match.getRedScore(alliances) > Match.getBlueScore(alliances)) {
                    redFinalsWon++;
                } else if (Match.getBlueScore(alliances) > Match.getRedScore(alliances)) {
                    blueFinalsWon++;
                }
            }
            if (match.getMatchType().isPlayoff()) {
                addAllianceTeams(
                        mAdvancement,
                        Match.getRedTeams(alliances),
                        PlayoffAdvancement.fromMatchType(match.getMatchType()));
                addAllianceTeams(
                        mAdvancement,
                        Match.getBlueTeams(alliances),
                        PlayoffAdvancement.fromMatchType(match.getMatchType()));
            }

            /**
             * the only reason this isn't moved to PopulateTeamAtEvent is that if so,
             * we'd have to iterate through every match again to calculate the
             * record, and that's just wasteful
             */
            if (mTeamKey != null) {
                match.addToRecord(mTeamKey, record);
            }
            lastType = currentType;
            previousIteration = match;
            lastMatchPlayed = match.hasBeenPlayed();
        }

        if (lastMatch == null && !mAPIData.isEmpty()) {
            Match last = mAPIData.get(mAPIData.size() - 1);
            if (last.hasBeenPlayed()) {
                lastMatch = last;
            }
        }

        if (lastMatch != null && lastMatch.getMatchType() == MatchType.FINAL) {
            if (redFinalsWon >= 2) {
                addAllianceTeams(mAdvancement, Match.getRedTeams(lastMatch.getAlliances()), PlayoffAdvancement.WINNER);
            } else if (blueFinalsWon >= 2) {
                addAllianceTeams(mAdvancement, Match.getBlueTeams(lastMatch.getAlliances()), PlayoffAdvancement.WINNER);
            }
        }

        if (nextMatch != null && nextMatch.hasBeenPlayed()) {
            // Avoids bug where matches loop over when all played
            // Because nextMatch is initialized to the first qual match
            // So that it displayed before any have been played
            nextMatch = null;
        }

        if (!mQualMatches.children.isEmpty()) {
            mDataToBind.add(mQualMatches);
        }
        if (!mOctoMatches.children.isEmpty()) {
            mDataToBind.add(mOctoMatches);
        }
        if (!mQuarterMatches.children.isEmpty()) {
            mDataToBind.add(mQuarterMatches);
        }
        if (!mSemiMatches.children.isEmpty()) {
            mDataToBind.add(mSemiMatches);
        }
        if (!mFinalMatches.children.isEmpty()) {
            mDataToBind.add(mFinalMatches);
        }

        mEventBus.post(new LiveEventMatchUpdateEvent(lastMatch, nextMatch));
        mEventBus.post(new AllianceAdvancementEvent(mAdvancement));
    }

    private void addAllianceTeams(
            HashMap<String, PlayoffAdvancement> advancement,
            JsonArray teams,
            PlayoffAdvancement level) {
        for (int i = 0; i < teams.size(); i++) {
            String teamKey = teams.get(i).getAsString();
            advancement.put(teamKey, level);
        }
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && !mAPIData.isEmpty();
    }

    @VisibleForTesting
    public HashMap<String, PlayoffAdvancement> getAdvancement() {
        return mAdvancement;
    }

    @Override
    protected boolean shouldPostToEventBus() {
        return true;
    }

    @Override
    protected void postToEventBus(EventBus eventBus) {
        eventBus.post(new EventMatchesEvent(mAPIData));
    }
}