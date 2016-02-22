package com.thebluealliance.androidclient.subscribers;

import com.google.gson.JsonArray;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.eventbus.EventMatchesEvent;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.EventHelper.CaseInsensitiveMap;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.listitems.EmptyListElement;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.renderers.MatchRenderer;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.thebluealliance.androidclient.subscribers.TeamAtEventSummarySubscriber.Model;

public class TeamAtEventSummarySubscriber extends BaseAPISubscriber<Model, List<ListItem>> {

    public static class Model {
        public final JsonArray teamAtEventRank;
        public final Event event;

        public Model(JsonArray teamAtEventRank, Event event) {
            this.teamAtEventRank = teamAtEventRank;
            this.event = event;
        }
    }

    private String mTeamKey;
    private Resources mResources;
    private MatchRenderer mMatchRenderer;
    private boolean mIsMatchListLoaded;

    // Data loaded from other sources
    private List<Match> mMatches;

    public TeamAtEventSummarySubscriber(Resources resources, MatchRenderer matchRenderer) {
        super();
        mResources = resources;
        mMatchRenderer = matchRenderer;
        mIsMatchListLoaded = false;
        mDataToBind = new ArrayList<>();
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    @Override
    public synchronized void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        Match nextMatch = null, lastMatch = null;
        Collections.sort(mMatches, new MatchSortByPlayOrderComparator());

        int[] record = MatchHelper.getRecordForTeam(mMatches, mTeamKey);
        String recordString =
          String.format("%1$d - %2$d - %3$d", record[0], record[1], record[2]);

        Event event = mAPIData.event;
        int year = event.getEventYear();
        boolean activeEvent = event.isHappeningNow();
        String actionBarTitle =
          String.format(mResources.getString(R.string.team_actionbar_title), mTeamKey.substring(3));
        String actionBarSubtitle = String.format("@ %1$d %2$s", year, event.getEventShortName());
        EventBus.getDefault().post(new ActionBarTitleEvent(actionBarTitle, actionBarSubtitle));

        if (activeEvent) {
            nextMatch = MatchHelper.getNextMatchPlayed(mMatches);
            lastMatch = MatchHelper.getLastMatchPlayed(mMatches);
        }

        // Search for team in alliances
        JsonArray alliances = event.getAlliances();
        int allianceNumber = 0, alliancePick = 0;

        if (alliances == null || alliances.size() == 0) {
            // We don't have alliance data. Try to determine from matches.
            allianceNumber = MatchHelper.getAllianceForTeam(mMatches, mTeamKey);
        } else {
            for (int i = 0; i < alliances.size(); i++) {
                JsonArray teams = alliances.get(i).getAsJsonObject().get("picks").getAsJsonArray();
                for (int j = 0; j < teams.size(); j++) {
                    if (teams.get(j).getAsString().equals(mTeamKey)) {
                        allianceNumber = i + 1;
                        alliancePick = j;
                    }
                }
            }
        }

        JsonArray rankData = mAPIData.teamAtEventRank;
        int rank = 0;
        String rankingString = "";
        if (rankData.size() > 0) {
            // fist index of second child is the rank
            rank = rankData.get(1).getAsJsonArray().get(0).getAsInt();
            JsonArray headerRow = rankData.get(0).getAsJsonArray();
            JsonArray teamRank = rankData.get(1).getAsJsonArray();
            CaseInsensitiveMap<String> rankingElements = new CaseInsensitiveMap<>();
            for (int i = 2; i < teamRank.size(); i++) {
                rankingElements.put(headerRow.get(i).getAsString(), teamRank.get(i).getAsString());
            }
            EventHelper.extractRankingString(rankingElements);
            rankingString = EventHelper.createRankingBreakdown(rankingElements);
        }

        // Rank
        if (rank > 0) {
            mDataToBind.add(new LabelValueListItem(
              mResources.getString(R.string.team_at_event_rank),
              rank + Utilities.getOrdinalFor(rank)));
        }

        LabelValueListItem rankBreakdownItem = new LabelValueListItem("Ranking Breakdown", rankingString);

        MatchHelper.EventStatus status;
        try {
            status = MatchHelper.evaluateStatusOfTeam(event, mMatches, mTeamKey);
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.d(Constants.LOG_TAG, "Status could not be evaluated for team; missing fields: "
              + Arrays.toString(e.getStackTrace()));
            status = MatchHelper.EventStatus.NOT_AVAILABLE;
        }

        if (status != MatchHelper.EventStatus.NOT_AVAILABLE) {

            // Record
            /* Don't show for 2015 events, because no wins and such */
            if (year != 2015 && !recordString.equals("0-0-0")) {
                mDataToBind.add(new LabelValueListItem(
                  mResources.getString(R.string.team_at_event_record),
                  recordString));
            }

            // Alliance
            if (status != MatchHelper.EventStatus.PLAYING_IN_QUALS &&
              status != MatchHelper.EventStatus.NO_ALLIANCE_DATA) {
                mDataToBind.add(new LabelValueListItem(
                  mResources.getString(R.string.team_at_event_alliance),
                  EventHelper.generateAllianceSummary(
                    mResources,
                    allianceNumber,
                    alliancePick)));
            }

            // Status
            if (status != MatchHelper.EventStatus.NOT_PICKED) {
                mDataToBind.add(new LabelValueListItem(
                  mResources.getString(R.string.team_at_event_status),
                  status.getDescriptionString(mResources)));
            }

            // Ranking Breakdown
            if (rankingString != null && !rankingString.isEmpty()) {
                mDataToBind.add(rankBreakdownItem);
            }

            if (lastMatch != null) {
                mDataToBind.add(new LabelValueListItem
                  (mResources.getString(R.string.title_last_match),
                    mMatchRenderer.renderFromModel(lastMatch, MatchRenderer.RENDER_DEFAULT)));
            }
            if (nextMatch != null) {
                mDataToBind.add(new LabelValueListItem(
                  mResources.getString(R.string.title_next_match),
                  mMatchRenderer.renderFromModel(nextMatch, MatchRenderer.RENDER_DEFAULT)));
            }
        } else if (rank > 0) {
            // Only show ranking breakdown if rankings are available
            mDataToBind.add(rankBreakdownItem);
        }

        if (mDataToBind.size() > 0) {
            // If there is data to add, then add an empty item next to it so we can scroll
            // all the way down and not have the FAB overlap with anything
            mDataToBind.add(new EmptyListElement(""));
        }

    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && mIsMatchListLoaded && mAPIData.event != null
                && mAPIData.teamAtEventRank != null;
    }

    /**
     * Load matches for team@event
     * Posted by {@link com.thebluealliance.androidclient.fragments.event.EventMatchesFragment}
     */
    @SuppressWarnings(value = "unused")
    public void onEventAsync(EventMatchesEvent matches) {
        if (matches == null) {
            return;
        }
        mIsMatchListLoaded = true;
        mMatches = new ArrayList<>(matches.getMatches());
        try {
            if (isDataValid()) {
                parseData();
                bindData();
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
    }
}
