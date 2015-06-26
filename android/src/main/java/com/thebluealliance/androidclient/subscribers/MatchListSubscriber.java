package com.thebluealliance.androidclient.subscribers;

import android.app.Activity;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ExpandableListAdapter;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByDisplayOrderComparator;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listitems.ListGroup;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchListSubscriber extends BaseAPISubscriber<List<Match>, ExpandableListAdapter> {

    private ListGroup mQualMatches;
    private ListGroup mQuarterMatches;
    private ListGroup mSemiMatches;
    private ListGroup mFinalMatches;
    private String mTeamKey;
    private String mEventKey;
    private Database mDb;

    public MatchListSubscriber(Activity activity, Database db) {
        super(true);
        mDataToBind = new MatchListAdapter(activity, new ArrayList<>());
        mQualMatches = new ListGroup(activity.getString(R.string.quals_header));
        mQuarterMatches = new ListGroup(activity.getString(R.string.quarters_header));
        mSemiMatches = new ListGroup(activity.getString(R.string.semis_header));
        mFinalMatches = new ListGroup(activity.getString(R.string.finals_header));
        mDb = db;
        mTeamKey = null;
    }

    public void setEventKey(String eventKey) {
        mEventKey = eventKey;
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
        ((MatchListAdapter) mDataToBind).setTeamKey(teamKey);
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.groups.clear();
        mQualMatches.clear();
        mQuarterMatches.clear();
        mSemiMatches.clear();
        mFinalMatches.clear();

        int[] record = {0, 0, 0}; //wins, losses, ties
        Match nextMatch = null;
        Match lastMatch = null;

        Event event = mDb.getEventsTable().get(mEventKey);
        if (event != null && event.isHappeningNow()) {
            Collections.sort(mAPIData, new MatchSortByDisplayOrderComparator());
        } else {
            Collections.sort(mAPIData, new MatchSortByPlayOrderComparator());
        }

        ListGroup currentGroup = mQualMatches;
        MatchHelper.TYPE lastType = null;
        Match previousIteration = null;
        boolean lastMatchPlayed = false;
        if (mAPIData.size() > 0) {
            nextMatch = mAPIData.get(0);
        }
        for (int i = 0; i < mAPIData.size(); i++) {
            Match match = mAPIData.get(i);
            MatchHelper.TYPE currentType = match.getType();
            if (lastType != currentType) {
                switch (match.getType()) {
                    case QUAL:
                        currentGroup = mQualMatches;
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
        if (nextMatch != null && nextMatch.hasBeenPlayed()) {
            // Avoids bug where matches loop over when all played
            // Because nextMatch is initialized to the first qual match
            // So that it displayed before any have been played
            nextMatch = null;
        }

        if (!mQualMatches.children.isEmpty()) {
            mDataToBind.groups.add(mQualMatches);
        }
        if (!mQuarterMatches.children.isEmpty()) {
            mDataToBind.groups.add(mQuarterMatches);
        }
        if (!mSemiMatches.children.isEmpty()) {
            mDataToBind.groups.add(mSemiMatches);
        }
        if (!mFinalMatches.children.isEmpty()) {
            mDataToBind.groups.add(mFinalMatches);
        }

        //TODO post nextMatch and lastMatch to an Observable event bus
    }
}