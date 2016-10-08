package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.database.tables.EventsTable;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.LabelValueDetailListItem;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public class TeamAtDistrictSummarySubscriber
  extends BaseAPISubscriber<DistrictTeam, List<ListItem>> {

    private Database mDb;
    private Resources mResources;
    private EventBus mEventBus;
    private String mTeamKey;
    private String mDistrictKey;

    public TeamAtDistrictSummarySubscriber(Database db, Resources resources, EventBus eventBus) {
        super();
        mDb = db;
        mResources = resources;
        mEventBus = eventBus;
        mDataToBind = new ArrayList<>();
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    public void setDistrictKey(String districtKey) {
        mDistrictKey = districtKey;
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        EventsTable eventsTable = mDb.getEventsTable();
        mDataToBind.add(new LabelValueListItem(mResources.getString(R.string.district_point_rank),
          mAPIData.getRank() + Utilities.getOrdinalFor(mAPIData.getRank())));

        if (mAPIData.getEvent1Key() != null
                && mAPIData.getEvent1Points() != null) {
            Event event1 = eventsTable.get(mAPIData.getEvent1Key());
            String event1Name = event1 != null ? event1.getShortName() : mAPIData.getEvent1Key();
            mDataToBind.add(new LabelValueDetailListItem(event1Name,
                    String.format(
                            mResources.getString(R.string.district_points_format), mAPIData.getEvent1Points()),
                    EventTeamHelper.generateKey(mAPIData.getEvent1Key(), mAPIData.getTeamKey())));
        }

        if (mAPIData.getEvent2Key() != null
                && mAPIData.getEvent2Points() != null) {
            Event event2 = eventsTable.get(mAPIData.getEvent2Key());
            String event2Name = event2 != null ? event2.getShortName() : mAPIData.getEvent2Key();
            mDataToBind.add(new LabelValueDetailListItem(event2Name,
                    String.format(
                            mResources.getString(R.string.district_points_format), mAPIData.getEvent2Points()),
                    EventTeamHelper.generateKey(mAPIData.getEvent2Key(), mAPIData.getTeamKey())));
        }

        if (mAPIData.getCmpKey() != null
                && mAPIData.getCmpPoints() != null) {
            Event districtCmp = eventsTable.get(mAPIData.getCmpKey());
            String cmpName = districtCmp != null ? districtCmp.getShortName() : mAPIData.getCmpKey();
            mDataToBind.add(new LabelValueDetailListItem(cmpName,
                    String.format(
                            mResources.getString(R.string.district_points_format), mAPIData.getCmpPoints()),
                    EventTeamHelper.generateKey(mAPIData.getCmpKey(), mAPIData.getTeamKey())));
        }

        if (mAPIData.getTotalPoints() != null) {
            mDataToBind.add(new LabelValueListItem(mResources.getString(R.string.total_district_points),
                    String.format(
                            mResources.getString(R.string.district_points_format), mAPIData.getTotalPoints())));
        }

        String actionBarTitle =
          String.format(mResources.getString(R.string.team_actionbar_title), mTeamKey.substring(3));
        String actionBarSubtitle = String.format("@ %1$s %2$s",
          mDistrictKey.substring(0, 4),
          mDistrictKey.substring(4).toUpperCase());
        mEventBus.post(new ActionBarTitleEvent(actionBarTitle, actionBarSubtitle));
    }
}
