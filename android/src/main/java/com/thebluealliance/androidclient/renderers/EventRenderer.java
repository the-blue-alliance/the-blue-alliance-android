package com.thebluealliance.androidclient.renderers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.listitems.AllianceListElement;
import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.WebcastListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.types.ModelType;
import com.thebluealliance.androidclient.types.PlayoffAdvancement;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
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
        try {
            return new EventListElement(
              event.getKey(),
              event.getEventYear(),
              event.getEventShortName(),
              event.getDateString(),
              event.getLocation(),
              safeMyTba);
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
            Log.w(Constants.LOG_TAG, "Missing fields for rendering event\n"
              + "Required fields: Database.Events.KEY, Database.Events.NAME, Database.Events.LOCATION");
            return null;
        }
    }

    @WorkerThread
    public List<WebcastListElement> renderWebcasts(Event event) {
        List<WebcastListElement> webcasts = new ArrayList<>();
        try {
            int i = 1;
            for (JsonElement webcast : event.getWebcasts()) {
                try {
                    webcasts.add(new WebcastListElement(event.getKey(), event.getEventShortName(), webcast.getAsJsonObject(), i));
                    i++;
                } catch (BasicModel.FieldNotDefinedException e) {
                    Log.w(Constants.LOG_TAG, "Missing fields for rendering event webcasts: KEY, SHORTNAME");
                }
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields to get event webcasts");
        }
        return webcasts;
    }

    @WorkerThread
    public List<ListItem> renderAlliances(Event event) {
        List<ListItem> output = new ArrayList<>();
        renderAlliances(event, output, null);
        return output;
    }

    @WorkerThread
    public void renderAlliances(Event event, List<ListItem> destList, HashMap<String, PlayoffAdvancement> advancement) {
        try {
            JsonArray alliances = event.getAlliances();
            int counter = 1;
            for (JsonElement alliance : alliances) {
                JsonArray teams = alliance.getAsJsonObject().get("picks").getAsJsonArray();
                PlayoffAdvancement adv = advancement != null
                        ? getAdvancement(advancement, teams)
                        : PlayoffAdvancement.NONE;
                destList.add(new AllianceListElement(event.getKey(), counter, teams, adv));
                counter++;
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            Log.w(Constants.LOG_TAG, "Missing fields for rendering alliances.\n"
              + "Required field: Database.Events.ALLIANCES");
        } catch (IllegalArgumentException e) {
            Log.w(Constants.LOG_TAG, "Invalid alliance size. Can't render");
        }
    }

    private static PlayoffAdvancement getAdvancement(HashMap<String, PlayoffAdvancement> advancement, JsonArray teams) {
        PlayoffAdvancement adv = PlayoffAdvancement.NONE;
        int level = 0;
        for (int i = 0; i < teams.size(); i++) {
            String teamKey = teams.get(i).getAsString();
            if (advancement.containsKey(teamKey)) {
                PlayoffAdvancement next = advancement.get(teamKey);
                if (next.getLevel() > level) {
                    adv = next;
                    level = next.getLevel();
                }
            }
        }
        return adv;
    }
}
