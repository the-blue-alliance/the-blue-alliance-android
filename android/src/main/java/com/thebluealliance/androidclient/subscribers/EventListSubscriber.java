package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.ModelRenderer;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Bind a list of events to a {@link ListViewAdapter}
 */
public class EventListSubscriber extends BaseAPISubscriber<List<Event>, List<Object>> {

    public static final int
      MODE_WEEK = 0,
      MODE_TEAM = 1,
      MODE_DISTRICT = 2;

    private int mRenderMode;
    private Context mContext;

    @Inject
    public EventListSubscriber(Context context) {
        super();
        mContext = context;
        mDataToBind = new ArrayList<>();
        mRenderMode = MODE_WEEK;
    }

    public void setRenderMode(int renderMode) {
        mRenderMode = renderMode;
    }

    @Override
    public void parseData() {
        mDataToBind.clear();
        switch (mRenderMode) {
            case MODE_WEEK:
            default:
                EventHelper.renderEventListForWeek(mContext, mAPIData, mDataToBind);
                break;
            case MODE_TEAM:
                EventHelper.renderEventListForTeam(mContext, mAPIData, mDataToBind);
                break;
            case MODE_DISTRICT:
                EventHelper.renderEventListForDistrict(mContext, mAPIData, mDataToBind);
                break;
        }
    }
}
