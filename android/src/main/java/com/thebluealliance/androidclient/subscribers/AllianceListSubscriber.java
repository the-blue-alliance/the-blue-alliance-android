package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.renderers.EventRenderer;

import java.util.ArrayList;
import java.util.List;

public class AllianceListSubscriber extends BaseAPISubscriber<List<EventAlliance>, List<ListItem>> {

    EventRenderer mRenderer;

    public AllianceListSubscriber(EventRenderer renderer) {
        super();
        mRenderer = renderer;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();

        mRenderer.renderAlliances(mAPIData, mDataToBind);
    }
}
