package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventWeekLabelSortComparator;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.thebluealliance.androidclient.helpers.EventHelper.TYPE;

public class EventTabSubscriber extends BaseAPISubscriber<List<Event>, List<String>> {

    private Comparator<String> mLabelComparator;

    public EventTabSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
        mLabelComparator = new EventWeekLabelSortComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        boolean champsFound = false;
        boolean preseasonFound = false;
        boolean offseasonFound = false;
        boolean weeklessFound = false;

        // Go through all the events and determine what labels we need to include
        for (int i = 0; i < mAPIData.size(); i++) {
            Event e = mAPIData.get(i);
            boolean official = e.isOfficial();
            TYPE type = e.getEventType();
            Date start = e.getStartDate();
            if (official &&
              (type == TYPE.CMP_DIVISION || type == TYPE.CMP_FINALS) &&
              !champsFound) {
                mDataToBind.add(EventHelper.CHAMPIONSHIP_LABEL);
                champsFound = true;
            } else if (official &&
              (type == TYPE.REGIONAL || type == TYPE.DISTRICT || type == TYPE.DISTRICT_CMP)) {
                if (start == null && !weeklessFound) {
                    mDataToBind.add(EventHelper.WEEKLESS_LABEL);
                    weeklessFound = true;
                } else {
                    String label =
                      String.format(EventHelper.REGIONAL_LABEL, e.getCompetitionWeek());
                    if (!mDataToBind.contains(label)) {
                        mDataToBind.add(label);
                    }
                }
            } else if (type == TYPE.PRESEASON && !preseasonFound) {
                mDataToBind.add(EventHelper.PRESEASON_LABEL);
                preseasonFound = true;
            } else if (type == TYPE.OFFSEASON && !offseasonFound){
                mDataToBind.add(EventHelper.OFFSEASON_LABEL);
                offseasonFound = true;
            }
        }
        Collections.sort(mDataToBind, mLabelComparator);
    }
}
