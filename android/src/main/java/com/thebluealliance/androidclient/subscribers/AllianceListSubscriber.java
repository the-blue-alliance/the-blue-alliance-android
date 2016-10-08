package com.thebluealliance.androidclient.subscribers;

import com.thebluealliance.androidclient.firebase.AllianceAdvancementEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.renderers.EventRenderer;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllianceListSubscriber extends BaseAPISubscriber<Event, List<ListItem>> {

    EventRenderer mRenderer;
    private boolean mIsAdvancementLoaded;
    private HashMap<String, PlayoffAdvancement> mAdvancement;

    public AllianceListSubscriber(EventRenderer renderer) {
        super();
        mRenderer = renderer;
        mIsAdvancementLoaded = false;
        mAdvancement = null;
        mDataToBind = new ArrayList<>();
    }

    @Override
    public void parseData()  {
        mDataToBind.clear();

        mRenderer.renderAlliances(mAPIData, mDataToBind, mAdvancement);
    }

    @Override
    public boolean isDataValid() {
        return super.isDataValid() && mIsAdvancementLoaded;
    }

    /**
     * Load matches for team@event
     * Posted by {@link com.thebluealliance.androidclient.fragments.event.EventMatchesFragment}
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onAllianceAdvancementLoaded(AllianceAdvancementEvent advancementEvent) {
        mIsAdvancementLoaded = true;
        if (advancementEvent == null) {
            return;
        }
        mAdvancement = advancementEvent.advancement;
        if (isDataValid()) {
            parseData();
            bindData();
        }
    }
}
