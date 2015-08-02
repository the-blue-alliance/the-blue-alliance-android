package com.thebluealliance.androidclient.subscribers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.eventbus.ActionBarTitleEvent;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Media;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MatchInfoSubscriber extends BaseAPISubscriber<Match, List<ListItem>> {

    private Gson mGson;
    private EventBus mEventBus;
    private String mMatchTitle;
    private String mEventName;
    private String mMatchKey;

    public MatchInfoSubscriber(Gson gson, EventBus eventBus) {
        super();
        mDataToBind = new ArrayList<>();
        mGson = gson;
        mEventBus = eventBus;
        mMatchTitle = null;
        mEventName = null;
        mMatchKey = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null) {
            return;
        }

        mDataToBind.add(mAPIData.render(false, true, false, false));

        mMatchTitle = mAPIData.getTitle();
        mMatchKey = mAPIData.getKey();
        JsonArray matchVideos = mAPIData.getVideos();
        for (int i = 0; i < matchVideos.size(); i++) {
            JsonElement video = matchVideos.get(i);
            if (Media.TYPE.fromString(video.getAsJsonObject().get("type").getAsString()) !=
              Media.TYPE.NONE) {
                mDataToBind.add(mGson.fromJson(video, Media.class).render());
            }
        }

        updateActionBarTitle();
    }

    /**
     * So we can load event data
     */
    @SuppressWarnings(value = "unused")
    public void onEventAsync(Event event) {
        try {
            mEventName = event.getEventShortName();
        } catch (BasicModel.FieldNotDefinedException e) {
            mEventName = null;
        }
        updateActionBarTitle();
    }

    private void updateActionBarTitle() {
        if (mEventName != null && mMatchTitle != null && mMatchKey != null) {
            String subtitle = "@ " + mMatchKey.substring(0, 4) + " " + mEventName;
            mEventBus.post(new ActionBarTitleEvent(mMatchTitle, subtitle));
        }
    }
}
