package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.JSONHelper;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.EventAlliance;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EventRenderer implements ModelRenderer<Event, Boolean> {

    private APICache mDatafeed;

    @Inject
    public EventRenderer(APICache datafeed) {
        mDatafeed = datafeed;
    }

    @WorkerThread
    @Override
    public @Nullable EventListElement renderFromKey(String key, ModelType type, Boolean args) {
        Event event = mDatafeed.fetchEvent(key).toBlocking().first();
        if (event == null) {
            return null;
        }

        return renderFromModel(event, false);
    }

    @WorkerThread
    @Override
    public @Nullable EventListElement renderFromModel(Event event, Boolean showMyTbaSettings) {
        boolean safeMyTba = showMyTbaSettings == null ? false : showMyTbaSettings;
            return new EventListElement(
              event.getKey(),
              event.getYear(),
              event.getShortName(),
              event.getDateString(),
              event.getLocation(),
              safeMyTba);
    }

    @WorkerThread
    public List<WebcastListElement> renderWebcasts(Event event) {
        List<WebcastListElement> webcasts = new ArrayList<>();
        JsonArray webcastJson = JSONHelper.getasJsonArray(event.getWebcasts());
        JsonElement webcast;
        for (int i = 1; i <= webcastJson.size(); i++) {
            webcast = webcastJson.get(i - 1);
            webcasts.add(new WebcastListElement(event.getKey(), event.getShortName(), webcast.getAsJsonObject(), i));
        }
        return webcasts;
    }

    @WorkerThread
    public void renderAlliances(List<EventAlliance> alliances, List<ListItem> destList) {
        int counter = 1;
        for (EventAlliance alliance : alliances) {
            List<String> teams = alliance.getPicks();
            PlayoffAdvancement adv = PlayoffAdvancement.fromAlliance(alliance);
            destList.add(new AllianceListElement(alliance.getEventKey(), alliance.getName(),
                                                 counter, teams, adv));
            counter++;
        }
    }
}
