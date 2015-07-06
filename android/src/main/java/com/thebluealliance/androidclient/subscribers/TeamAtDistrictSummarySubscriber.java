package com.thebluealliance.androidclient.subscribers;

import android.content.res.Resources;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.Utilities;
import com.thebluealliance.androidclient.database.Database;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.LabelValueDetailListItem;
import com.thebluealliance.androidclient.listitems.LabelValueListItem;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.DistrictTeam;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class TeamAtDistrictSummarySubscriber
  extends BaseAPISubscriber<DistrictTeam, List<ListItem>> {

    private Database mDb;
    private Resources mResources;
    private String mTeamKey;
    private String mDistrictKey;

    public TeamAtDistrictSummarySubscriber(Database db, Resources resources) {
        super();
        mDb = db;
        mResources = resources;
        mDataToBind = new ArrayList<>();
    }

    public void setTeamKey(String teamKey) {
        mTeamKey = teamKey;
    }

    public void setDistrictKey(String districtKey) {
        mDistrictKey = districtKey;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }

        mDataToBind.add(new LabelValueListItem(mResources.getString(R.string.district_point_rank),
          mAPIData.getRank() + Utilities.getOrdinalFor(mAPIData.getRank())));

        Event event1 = mDb.getEventsTable().get(mAPIData.getEvent1Key());
        mDataToBind.add(new LabelValueDetailListItem(event1.getEventName(),
          String.format(
            mResources.getString(R.string.district_points_format), mAPIData.getEvent1Points()),
          EventTeamHelper.generateKey(mAPIData.getEvent1Key(), mAPIData.getTeamKey())));

        Event event2 = mDb.getEventsTable().get(mAPIData.getEvent2Key());
        mDataToBind.add(new LabelValueDetailListItem(event2.getEventName(),
          String.format(
            mResources.getString(R.string.district_points_format), mAPIData.getEvent2Points()),
          EventTeamHelper.generateKey(mAPIData.getEvent2Key(), mAPIData.getTeamKey())));

        Event districtCmp = mDb.getEventsTable().get(mAPIData.getCmpKey());
        mDataToBind.add(new LabelValueDetailListItem(districtCmp.getEventName(),
          String.format(
            mResources.getString(R.string.district_points_format), mAPIData.getCmpPoints()),
          EventTeamHelper.generateKey(mAPIData.getCmpKey(), mAPIData.getTeamKey())));

        mDataToBind.add(new LabelValueListItem(mResources.getString(R.string.total_district_points),
          String.format(
            mResources.getString(R.string.district_points_format), mAPIData.getTotalPoints())));

        String actionBarTitle =
          String.format(mResources.getString(R.string.team_actionbar_title), mTeamKey.substring(3));
        String actionBarSubtitle = String.format("@ %1$s %2$s",
          mDistrictKey.substring(0, 4),
          mDistrictKey.substring(4).toUpperCase());
        EventBus.getDefault().post(new ActionBarTitleEvent(actionBarTitle, actionBarSubtitle));
    }
}
