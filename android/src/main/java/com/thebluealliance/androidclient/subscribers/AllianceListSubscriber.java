package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;
import java.util.List;

public class AllianceListSubscriber extends BaseAPISubscriber<Event, List<ListItem>> {

    public AllianceListSubscriber() {
        super();
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }
        mAPIData.renderAlliances(mDataToBind);
    }
}
