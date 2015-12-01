package com.thebluealliance.androidclient.renderers;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.helpers.ModelType;
import com.thebluealliance.androidclient.listitems.ListItem;
import com.thebluealliance.androidclient.listitems.ModelListElement;
import com.thebluealliance.androidclient.models.BasicModel;
import com.thebluealliance.androidclient.models.District;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyTbaModelRenderer implements ModelRenderer<Void, Void> {

    private APICache mDatafeed;
    private EventRenderer mEventRenderer;
    private TeamRenderer mTeamRenderer;

    @Inject
    public MyTbaModelRenderer(
      APICache datafeed,
      EventRenderer eventRenderer,
      TeamRenderer teamRenderer) {
        mDatafeed = datafeed;
        mEventRenderer = eventRenderer;
        mTeamRenderer = teamRenderer;
    }

    @WorkerThread @Override
    public @Nullable ListItem renderFromKey(String key, ModelType type) {
        String text;
        try {
            switch (type) {
                case EVENT:
                    Event event = mDatafeed.fetchEvent(key).toBlocking().first();
                    if (event == null) {
                        return new ModelListElement(key, key, type);
                    }
                    return mEventRenderer.renderFromModel(event, null);
                case TEAM:
                    Team team = mDatafeed.fetchTeam(key).toBlocking().first();
                    if (team == null) {
                        return new ModelListElement(key, key, type);
                    }
                    return mTeamRenderer.renderFromModel(team, TeamRenderer.RENDER_BASIC);
                case MATCH:
                    Match match = mDatafeed.fetchMatch(key).toBlocking().first();
                    if (match == null) {
                        return new ModelListElement(key, key, type);
                    }
                    return match.render();
                case EVENTTEAM:
                    String teamKey = EventTeamHelper.getTeamKey(key);
                    String eventKey = EventTeamHelper.getEventKey(key);
                    Team eTeam = mDatafeed.fetchTeam(teamKey).toBlocking().first();
                    Event eEvent = mDatafeed.fetchEvent(eventKey).toBlocking().first();
                    if (eTeam == null || eEvent == null) {
                        text = String.format("%1$s @ %2$s", teamKey, eventKey);
                        return new ModelListElement(text, key, type);
                    }
                    text = String.format("%1$s @ %2$d %3$s",
                      eTeam.getNickname(),
                      eEvent.getEventYear(),
                      eEvent.getEventShortName());
                    return new ModelListElement(text, key, type);
                case DISTRICT:
                    District district = mDatafeed.fetchDistrict(key).toBlocking().first();
                    if (district == null) {
                        return new ModelListElement(key, key, type);
                    }
                    return district.render();
                default:
                    return null;
            }
        } catch (BasicModel.FieldNotDefinedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable ListItem renderFromModel(Void aVoid, Void a2void) {
        return null;
    }
}
