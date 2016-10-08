package com.thebluealliance.androidclient.renderers;

import com.thebluealliance.androidclient.datafeed.APICache;
import com.thebluealliance.androidclient.helpers.EventTeamHelper;
import com.thebluealliance.androidclient.listitems.DistrictListElement;
import com.thebluealliance.androidclient.listitems.ListElement;
import com.thebluealliance.androidclient.listitems.ModelListElement;
import com.thebluealliance.androidclient.models.Event;
import com.thebluealliance.androidclient.models.Match;
import com.thebluealliance.androidclient.models.Team;
import com.thebluealliance.androidclient.types.ModelType;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyTbaModelRenderer implements ModelRenderer<Void, Void> {

    private APICache mDatafeed;
    private EventRenderer mEventRenderer;
    private TeamRenderer mTeamRenderer;
    private MatchRenderer mMatchRenderer;
    private DistrictRenderer mDistrictRenderer;

    @Inject
    public MyTbaModelRenderer(
      APICache datafeed,
      EventRenderer eventRenderer,
      TeamRenderer teamRenderer,
      MatchRenderer matchRenderer,
      DistrictRenderer districtRenderer) {
        mDatafeed = datafeed;
        mEventRenderer = eventRenderer;
        mTeamRenderer = teamRenderer;
        mMatchRenderer = matchRenderer;
        mDistrictRenderer = districtRenderer;
    }

    @WorkerThread @Override
    public @Nullable ListElement renderFromKey(String key, ModelType type, Void args) {
        String text;
        switch (type) {
            case EVENT:
                Event event = mDatafeed.fetchEvent(key).toBlocking().first();
                if (event == null) {
                    return new ModelListElement(key, key, type);
                }
                return mEventRenderer.renderFromModel(event, true);
            case TEAM:
                Team team = mDatafeed.fetchTeam(key).toBlocking().first();
                if (team == null) {
                    return new ModelListElement(key, key, type);
                }
                return mTeamRenderer.renderFromModel(team, TeamRenderer.RENDER_MYTBA_DETAILS);
            case MATCH:
                Match match = mDatafeed.fetchMatch(key).toBlocking().first();
                if (match == null) {
                    return new ModelListElement(key, key, type);
                }
                return mMatchRenderer.renderFromModel(match, MatchRenderer.RENDER_DEFAULT);
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
                                     eEvent.getYear(),
                                     eEvent.getShortName());
                return new ModelListElement(text, key, type);
            case DISTRICT:
                DistrictListElement element = mDistrictRenderer.renderFromKey(
                        key,
                        ModelType.DISTRICT,
                        new DistrictRenderer.RenderArgs(0, true));
                if (element == null) {
                    return new ModelListElement(key, key, type);
                }
                return element;
            default:
                return null;
        }
    }

    /**
     * Not needed for mytba
     */
    @Override
    public @Nullable ListElement renderFromModel(Void aVoid, Void a2void) {
        return null;
    }
}
