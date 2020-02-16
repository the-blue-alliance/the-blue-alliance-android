package com.thebluealliance.androidclient.subscribers;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.config.AppConfig;
import com.thebluealliance.androidclient.eventbus.EventAwardsEvent;
import com.thebluealliance.androidclient.eventbus.EventMatchesEvent;
import com.thebluealliance.androidclient.helpers.MatchHelper;
import com.thebluealliance.androidclient.helpers.PitLocationHelper;
import com.thebluealliance.androidclient.models.Award;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.RankingItem;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.models.TeamAtEventStatus;
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.viewmodels.LabelValueViewModel;
import com.thebluealliance.androidclient.viewmodels.LabeledMatchViewModel;
import com.thebluealliance.androidclient.viewmodels.SimpleTeamViewModel;
import com.thebluealliance.api.model.IRankingItem;
import com.thebluealliance.api.model.IRankingSortOrder;
import com.thebluealliance.api.model.ITeamAtEventAlliance;
import com.thebluealliance.api.model.ITeamAtEventPlayoff;
import com.thebluealliance.api.model.ITeamAtEventQual;
import com.thebluealliance.api.model.ITeamRecord;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static com.thebluealliance.androidclient.helpers.RankingFormatter.NONE;
import static com.thebluealliance.androidclient.helpers.RankingFormatter.buildRankingString;

public class TeamAtEventSummarySubscriber extends BaseAPISubscriber<TeamAtEventSummarySubscriber.Model, List<Object>> {

    public static class Model {
        public final @Nullable TeamAtEventStatus status;
        public final Event event;
        public final Team team;

        public Model(@Nullable TeamAtEventStatus status, Event event, Team team) {
            this.status = status;
            this.event = event;
            this.team = team;
        }
    }

    private final Context mContext;
    private final Resources mResources;
    private final MatchRenderer mMatchRenderer;
    private final AppConfig mAppConfig;
    private final EventBus mEventBus;
    private String mTeamKey;
    private String mEventKey;
    private boolean mIsMatchListLoaded;
    private boolean mIsAwardListLoaded;

    // Data loaded from other sources
    private List<Match> mMatches;
    private List<Award> mAwards;

    public TeamAtEventSummarySubscriber(Context context,
                                        AppConfig config,
                                        EventBus eventBus,
                                        MatchRenderer matchRenderer) {
        super();
        mContext = context;
        mResources = context.getResources();
        mMatchRenderer = matchRenderer;
        mAppConfig = config;
        mEventBus = eventBus;
        mIsMatchListLoaded = false;
        mIsAwardListLoaded = false;
        mDataToBind = new ArrayList<>();
    }

    public void setTeamAndEventKeys(String teamKey, String eventKey) {
        mTeamKey = teamKey;
        mEventKey = eventKey;
    }

    @Override
    public synchronized void parseData()  {
        mDataToBind.clear();
        Match nextMatch = null, lastMatch = null;

        @Nullable TeamAtEventStatus status = mAPIData.status;
        @Nullable ITeamAtEventAlliance allianceData = status != null ? status.getAlliance() : null;
        @Nullable ITeamAtEventQual qualData = status != null ? status.getQual() : null;
        @Nullable ITeamAtEventPlayoff playoffData = status != null ? status.getPlayoff() : null;

        Event event = mAPIData.event;
        Date now = new Date();
        if (event == null) {
            return;
        }

        Team team = mAPIData.team;
        if (team == null) {
            return;
        }

        int year = event.getYear();
        boolean activeEvent = event.isHappeningNow();

        String playoffStatusString = status != null ? status.getPlayoffStatusStr() : "";
        String allianceStatusString = status != null ? status.getAllianceStatusStr() : "";
        String overallStatusString;
        if (status != null) {
            overallStatusString = status.getOverallStatusStr();
        } else if (now.before(event.getStartDate())) {
            overallStatusString = mResources.getString(R.string.team_at_event_future_event);
        } else {
            overallStatusString = "";
        }

        String qualRecordString;
        @Nullable ITeamRecord qualRecord = null;
        if (qualData != null
                && qualData.getRanking() != null
                && qualData.getRanking().getRecord() != null) {
            qualRecord = qualData.getRanking().getRecord();
        }
        if (qualRecord != null) {
            qualRecordString = RankingItem.TeamRecord.buildRecordString(qualRecord);
        } else {
            qualRecordString = "";
        }

        if (activeEvent && mIsMatchListLoaded) {
            Collections.sort(mMatches, new MatchSortByPlayOrderComparator());
            nextMatch = MatchHelper.getNextMatchPlayed(mMatches);
            lastMatch = MatchHelper.getLastMatchPlayed(mMatches);
        }

        int rank = 0;
        String rankingString = "";
        LabelValueViewModel rankBreakdownItem = null;
        @Nullable IRankingItem rankData = qualData != null ? qualData.getRanking() : null;
        @Nullable List<IRankingSortOrder> sortOrders = qualData != null ? qualData.getSortOrderInfo() : null;
        if (rankData != null && sortOrders != null) {
            rank = rankData.getRank();
            rankingString = buildRankingString(rankData, sortOrders, null, mResources, NONE);
            rankBreakdownItem = new LabelValueViewModel(mResources.getString(R.string.team_at_event_rank_breakdown),
                                                        rankingString);
        }

        // Basic information about the team
        mDataToBind.add(new SimpleTeamViewModel(team.getKey(), team.getNickname(), team.getLocation(), year));

        // Rank
        if (rank > 0) {
            mDataToBind.add(new LabelValueViewModel(
                    mResources.getString(R.string.team_at_event_rank),
                    rank + Utilities.getOrdinalFor(rank)));
        }


        // Number of awards, only if nonzero
        if (mIsAwardListLoaded && mAwards.size() > 0) {
            String awardsString;
            if (mAwards.size() == 1) {
                awardsString = mResources.getString(R.string.team_at_event_awards_format_one);
            } else {
                awardsString = mResources.getString(R.string.team_at_event_awards_format, mAwards.size());
            }
            mDataToBind.add(new LabelValueViewModel(
                    mResources.getString(R.string.awards_header),
                    awardsString));
        }

        // Pit Location Stuff
        if (PitLocationHelper.shouldShowPitLocationAtEvent(mAppConfig, mEventKey)) {
            PitLocationHelper.TeamPitLocation location = PitLocationHelper
                    .getPitLocation(mContext, mTeamKey);
            if (location != null) {
                mDataToBind.add(new LabelValueViewModel(mResources.getString(R.string.pit_location),
                                                        location.getAddressString()));
            }
        }

        /* Team Qual Record
         * Don't show for 2015 events, because no wins and such */
        if (year != 2015 && !RankingItem.TeamRecord.isEmpty(qualRecord)) {
            mDataToBind.add(new LabelValueViewModel(
                    mResources.getString(R.string.team_at_event_qual_record),
                    qualRecordString));
        }

        // Alliance
        if (allianceData != null) {
            mDataToBind.add(new LabelValueViewModel(
                    mResources.getString(R.string.team_at_event_alliance),
                    Html.fromHtml(allianceStatusString)));
        }

        // Alliance Status
        mDataToBind.add(new LabelValueViewModel(
                    mResources.getString(R.string.team_at_event_status),
                    Html.fromHtml(playoffData != null ? playoffStatusString : overallStatusString)));

        // Ranking Breakdown
        if (rankingString != null && !rankingString.isEmpty()) {
            mDataToBind.add(rankBreakdownItem);
        }

        if (lastMatch != null) {
            mDataToBind.add(new LabeledMatchViewModel(
                    mResources.getString(R.string.title_last_match),
                    mMatchRenderer.renderFromModel(lastMatch, MatchRenderer.RENDER_DEFAULT)));
        }
        if (nextMatch != null) {
            mDataToBind.add(new LabeledMatchViewModel(
                    mResources.getString(R.string.title_next_match),
                    mMatchRenderer.renderFromModel(nextMatch, MatchRenderer.RENDER_DEFAULT)));
        }
    }

    /**
     * Load matches for team@event
     * Posted by {@link com.thebluealliance.androidclient.fragments.event.EventMatchesFragment}
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventMatchesLoaded(EventMatchesEvent matches) {
        if (matches == null) {
            return;
        }
        mIsMatchListLoaded = true;
        mMatches = new ArrayList<>(matches.getMatches());
        if (isDataValid()) {
            parseData();
            bindData();
        }
    }

    /**
     * Load awards for team@event
     * Posted by {@link com.thebluealliance.androidclient.fragments.event.EventAwardsFragment}
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAwardsLoaded(EventAwardsEvent awards) {
        if (awards == null) {
            return;
        }
        mIsAwardListLoaded = true;
        mAwards = new ArrayList<>(awards.getAwards());
        if (isDataValid()) {
            parseData();
            bindData();
        }
    }
}
