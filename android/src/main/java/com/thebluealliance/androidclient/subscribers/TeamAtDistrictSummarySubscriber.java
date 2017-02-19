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
import com.thebluealliance.androidclient.models.DistrictRanking;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.api.model.IDistrictEventPoints;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TeamAtDistrictSummarySubscriber
  extends BaseAPISubscriber<DistrictRanking, List<ListItem>> {

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

        @Nullable IDistrictEventPoints event1Points = getEventPoints(mAPIData, 0);
        if (event1Points != null) {
            mDataToBind.add(renderEventPoints(mAPIData.getTeamKey(), event1Points, eventsTable, mResources));
        }

        @Nullable IDistrictEventPoints event2Points = getEventPoints(mAPIData, 1);
        if (event2Points != null) {
            mDataToBind.add(renderEventPoints(mAPIData.getTeamKey(), event2Points, eventsTable, mResources));
        }

        @Nullable IDistrictEventPoints cmpPoints = getEventPoints(mAPIData, 2);
        if (cmpPoints != null) {
            mDataToBind.add(renderEventPoints(mAPIData.getTeamKey(), cmpPoints, eventsTable, mResources));
        }

        if (mAPIData.getPointTotal() != null) {
            mDataToBind.add(new LabelValueListItem(mResources.getString(R.string.total_district_points),
                    String.format(
                            mResources.getString(R.string.district_points_format), mAPIData.getPointTotal())));
        }

        String actionBarTitle =
          String.format(mResources.getString(R.string.team_actionbar_title), mTeamKey.substring(3));
        String actionBarSubtitle = String.format("@ %1$s %2$s",
          mDistrictKey.substring(0, 4),
          mDistrictKey.substring(4).toUpperCase());
        mEventBus.post(new ActionBarTitleEvent(actionBarTitle, actionBarSubtitle));
    }

    private static LabelValueDetailListItem renderEventPoints(String teamKey,
                                                              IDistrictEventPoints points,
                                                              EventsTable eventsTable,
                                                              Resources resources) {
        Event event = eventsTable.get(points.getEventKey());
        String event1Name = event != null ? event.getShortName() : points.getEventKey();
        return new LabelValueDetailListItem(event1Name,
                                            String.format(resources.getString(R.string.district_points_format), points.getTotal()),
                                            EventTeamHelper.generateKey(points.getEventKey(), teamKey));
    }

    private static @Nullable IDistrictEventPoints getEventPoints(DistrictRanking ranking, int index) {
        if (ranking.getEventPoints() == null || ranking.getEventPoints().size() < (index + 1)) {
            return null;
        }
        return ranking.getEventPoints().get(index);
    }
}
