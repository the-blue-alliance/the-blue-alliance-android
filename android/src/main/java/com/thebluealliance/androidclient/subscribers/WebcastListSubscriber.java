package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.comparators.EventSortByTypeAndNameComparator;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WebcastListSubscriber extends BaseAPISubscriber<List<Event>, List<ListItem>> {

    private Comparator<Event> mComparator;
    private EventRenderer mRenderer;

    public WebcastListSubscriber(EventRenderer renderer) {
        mRenderer = renderer;
        mDataToBind = new ArrayList<>();
        mComparator = new EventSortByTypeAndNameComparator();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();
        Collections.sort(mAPIData, mComparator);

        for (int i = 0; i < mAPIData.size(); i++) {
            mDataToBind.addAll(mRenderer.renderWebcasts(mAPIData.get(i)));
        }
    }
}
