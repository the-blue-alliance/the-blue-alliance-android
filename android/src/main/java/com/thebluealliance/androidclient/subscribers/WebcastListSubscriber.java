package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventSortByTypeAndNameComparator;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WebcastListSubscriber extends BaseAPISubscriber<List<Event>, List<ListItem>> {

    private Comparator<Event> mComparator;

    public WebcastListSubscriber() {
        mDataToBind = new ArrayList<>();
        mComparator = new EventSortByTypeAndNameComparator();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }
        Collections.sort(mAPIData, mComparator);

        for (int i = 0; i < mAPIData.size(); i++) {
            mDataToBind.addAll(mAPIData.get(i).renderWebcasts());
        }
    }
}
