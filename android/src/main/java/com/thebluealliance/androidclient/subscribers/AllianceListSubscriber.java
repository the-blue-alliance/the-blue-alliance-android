package com.thebluealliance.androidclient.subscribers;

import android.content.Context;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;

import java.util.ArrayList;

public class AllianceListSubscriber extends BaseAPISubscriber<Event, ListViewAdapter> {

    public AllianceListSubscriber(Context context) {
        super();
        mDataToBind = new ListViewAdapter(context, new ArrayList<>());
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.values.clear();
        mAPIData.renderAlliances(mDataToBind.values);
    }
}
