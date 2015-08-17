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

import static com.thebluealliance.androidclient.subscribers.MatchInfoSubscriber.Model;

public class MatchInfoSubscriber extends BaseAPISubscriber<Model, List<ListItem>> {

    public static class Model {
        public final Match match;
        public final Event event;

        public Model(Match match, Event event) {
            this.match = match;
            this.event = event;
        }
    }

    private Gson mGson;
    private EventBus mEventBus;
    private String mMatchTitle;
    private String mMatchKey;

    public MatchInfoSubscriber(Gson gson, EventBus eventBus) {
        super();
        mDataToBind = new ArrayList<>();
        mGson = gson;
        mEventBus = eventBus;
        mMatchTitle = null;
        mMatchKey = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();
        if (mAPIData == null || mAPIData.event == null || mAPIData.match == null) {
            return;
        }

        mDataToBind.add(mAPIData.match.render(false, true, false, false));

        mMatchTitle = mAPIData.match.getTitle();
        mMatchKey = mAPIData.match.getKey();
        JsonArray matchVideos = mAPIData.match.getVideos();
        for (int i = 0; i < matchVideos.size(); i++) {
            JsonElement video = matchVideos.get(i);
            if (Media.TYPE.fromString(video.getAsJsonObject().get("type").getAsString()) !=
              Media.TYPE.NONE) {
                mDataToBind.add(mGson.fromJson(video, Media.class).render());
            }
        }

        updateActionBarTitle(mAPIData.event.getEventShortName());
    }

    private void updateActionBarTitle(String eventName) {
        if (eventName != null && mMatchTitle != null && mMatchKey != null) {
            String subtitle = "@ " + mMatchKey.substring(0, 4) + " " + eventName;
            mEventBus.post(new ActionBarTitleEvent(mMatchTitle, subtitle));
        }
    }
}
