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
import com.thebluealliance.androidclient.renderers.MatchRenderer;
import com.thebluealliance.androidclient.renderers.MediaRenderer;
import com.thebluealliance.androidclient.types.MediaType;

import org.greenrobot.eventbus.EventBus;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

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

    private final Gson mGson;
    private final EventBus mEventBus;
    private final MatchRenderer mRenderer;
    private final MediaRenderer mMediaRenderer;
    private final Resources mResources;
    private String mMatchTitle;
    private String mMatchKey;

    public MatchInfoSubscriber(Gson gson, EventBus eventBus, MatchRenderer renderer,
                               MediaRenderer mediaRenderer, Resources resources) {
        super();
        mDataToBind = new ArrayList<>();
        mGson = gson;
        mEventBus = eventBus;
        mRenderer = renderer;
        mMediaRenderer = mediaRenderer;
        mResources = resources;
        mMatchTitle = null;
        mMatchKey = null;
    }

    @Override
    public void parseData() throws BasicModel.FieldNotDefinedException {
        mDataToBind.clear();

        mDataToBind.add(mRenderer.renderFromModel(mAPIData.match, MatchRenderer.RENDER_MATCH_INFO));

        mMatchTitle = mAPIData.match.getTitle(mResources);
        mMatchKey = mAPIData.match.getKey();
        JsonArray matchVideos = mAPIData.match.getVideos();
        for (int i = 0; i < matchVideos.size(); i++) {
            JsonElement video = matchVideos.get(i);
            if (MediaType.fromString(video.getAsJsonObject().get("type").getAsString())
              != MediaType.NONE) {
                Media media = mGson.fromJson(video, Media.class);
                mDataToBind.add(mMediaRenderer.renderFromModel(media, null));
            }
        }

        updateActionBarTitle(mAPIData.event.getEventShortName());
    }

    @Override public boolean isDataValid() {
        return super.isDataValid() && mAPIData.event != null && mAPIData.match != null;
    }

    private void updateActionBarTitle(String eventName) {
        if (eventName != null && mMatchTitle != null && mMatchKey != null) {
            String subtitle = "@ " + mMatchKey.substring(0, 4) + " " + eventName;
            mEventBus.post(new ActionBarTitleEvent(mMatchTitle, subtitle));
        }
    }

    @Override
    protected boolean shouldPostToEventBus() {
        return true;
    }
}
